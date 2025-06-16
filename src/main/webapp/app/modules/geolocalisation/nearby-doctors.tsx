import './nearby-doctors.scss';
import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import {
  Alert,
  Col,
  Row,
  Button,
  Card,
  CardBody,
  CardText,
  Badge,
  Spinner,
  ButtonGroup,
  Input,
  Label,
  Modal,
  ModalHeader,
  ModalBody,
  ModalFooter,
} from 'reactstrap';
import axios from 'axios';
import dayjs from 'dayjs';
import { useAppSelector } from 'app/config/store';

// Doctor availability interface
interface DoctorAvailability {
  id: number;
  dayOfWeek: string; // MONDAY, TUESDAY, etc.
  startTime: string; // HH:mm format
  endTime: string; // HH:mm format
  isAvailable: boolean;
  recurrenceType: string;
  validFrom: string;
  validTo: string | null;
  doctorId: string;
}

interface DoctorResult {
  provider: string; // "LOCAL" for database doctors, "GOOGLE" for Google Maps doctors
  placeId: string;
  name: string;
  address: string;
  latitude: number;
  longitude: number;
  distanceKm: number;
  travelDurationSec: number;
  rating: number;
  userRatingCount: number;
  openNow: boolean;
  weekdayDescriptions: string[];
  // Add availability for local doctors
  availability?: DoctorAvailability[];
}

export const NearbyDoctors = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const account = useAppSelector(state => state.authentication.account);
  const [doctors, setDoctors] = useState<DoctorResult[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');
  const [locationError, setLocationError] = useState('');
  const [searchParams, setSearchParams] = useState({
    spec: '',
    lat: 0,
    lon: 0,
    radius: 10,
    sort: 'distance',
    dir: 'asc',
    openNow: false,
  });
  const [gettingLocation, setGettingLocation] = useState(false);
  const [hasUserLocation, setHasUserLocation] = useState(false);
  const [doctorAvailabilities, setDoctorAvailabilities] = useState<{ [doctorId: string]: DoctorAvailability[] }>({});
  const [sortBy, setSortBy] = useState<'distance' | 'rating'>('distance');
  const [radiusKm, setRadiusKm] = useState(10);
  const [showGoogleDoctorDetails, setShowGoogleDoctorDetails] = useState(false);
  const [selectedDoctor, setSelectedDoctor] = useState<DoctorResult | null>(null);
  const [showLocalDoctorDetails, setShowLocalDoctorDetails] = useState(false);
  const [selectedLocalDoctor, setSelectedLocalDoctor] = useState<any>(null);

  // Function to save user location to database
  const saveUserLocation = async (latitude: number, longitude: number) => {
    try {
      await axios.patch('/api/app-user-profiles/current/location', {
        latitude,
        longitude,
      });
      console.log('User location saved to database');
    } catch (saveError) {
      console.error('Failed to save user location:', saveError);
    }
  };

  // Function to load user location from database
  const loadUserLocation = async () => {
    try {
      const response = await axios.get('/api/app-user-profiles/current');
      const userProfile = response.data;

      if (userProfile && userProfile.latitude && userProfile.longitude) {
        console.log('Using saved user location from database');
        setSearchParams(prev => ({
          ...prev,
          lat: userProfile.latitude,
          lon: userProfile.longitude,
        }));
        setHasUserLocation(true);
        fetchDoctors(userProfile.latitude, userProfile.longitude);
        return true; // Location found in database
      }
      return false; // No location in database
    } catch (loadError) {
      console.error('Failed to load user location from database:', loadError);
      return false;
    }
  };

  const getUserLocation = async () => {
    setGettingLocation(true);
    setLocationError('');

    // First, try to load location from database
    const hasStoredLocation = await loadUserLocation();
    if (hasStoredLocation) {
      setGettingLocation(false);
      return;
    }

    // If no stored location, try to get current location
    if (!navigator.geolocation) {
      setLocationError('Geolocation is not supported by your browser. Please update your location in your profile settings.');
      setGettingLocation(false);
      setHasUserLocation(false);
      return;
    }

    navigator.geolocation.getCurrentPosition(
      position => {
        const { latitude, longitude } = position.coords;
        setSearchParams(prev => ({
          ...prev,
          lat: latitude,
          lon: longitude,
        }));
        setHasUserLocation(true);

        // Save location to database for future use
        saveUserLocation(latitude, longitude);

        fetchDoctors(latitude, longitude);
        setGettingLocation(false);
      },
      async err => {
        console.error('Geolocation error:', err);

        // If geolocation fails, try to use stored location as fallback
        const hasFallbackLocation = await loadUserLocation();
        if (hasFallbackLocation) {
          setLocationError('Using your saved location. For more accurate results, please allow location access.');
        } else {
          setLocationError(
            'Unable to retrieve your location. Please allow location access or update your location in your profile settings.',
          );
          setHasUserLocation(false);
        }
        setGettingLocation(false);
      },
      {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 0,
      },
    );
  };

  const fetchDoctors = async (lat: number, lon: number, sort?: 'distance' | 'rating', radius?: number) => {
    // Only fetch if we have valid coordinates (not default values)
    if (lat === 0 && lon === 0) {
      setIsLoading(false);
      return;
    }

    setIsLoading(true);
    setError('');

    const currentSort = sort || sortBy;
    const currentRadius = radius || radiusKm;

    try {
      const response = await axios.get('/api/doctors/nearby', {
        params: {
          spec: searchParams.spec,
          lat,
          lon,
          radius: currentRadius,
          sort: currentSort === 'rating' ? 'rating' : 'distance',
          dir: currentSort === 'rating' ? 'desc' : 'asc',
          openNow: searchParams.openNow,
        },
      });

      console.log('API Response:', response.data);
      console.log('Response type:', typeof response.data);
      console.log('Is array:', Array.isArray(response.data));

      // Backend now always returns an array (even if empty)
      if (Array.isArray(response.data)) {
        setDoctors(response.data);
        // Fetch availability for local doctors
        await fetchAllDoctorAvailabilities(response.data);
      } else {
        console.warn('Unexpected response format - expected array but got:', typeof response.data, response.data);
        setDoctors([]);
      }
    } catch (fetchError) {
      setError('Failed to fetch doctors. Please try again later.');
      console.error('API Error:', fetchError);
      setDoctors([]);
    } finally {
      setIsLoading(false);
    }
  };

  // Function to fetch doctor availability
  const fetchDoctorAvailability = async (doctorId: string): Promise<DoctorAvailability[]> => {
    try {
      const response = await axios.get(`/api/doctor-availabilities/doctor/${doctorId}`);
      return response.data || [];
    } catch (fetchError) {
      console.error(`Failed to fetch availability for doctor ${doctorId}:`, fetchError);
      return [];
    }
  };

  // Function to fetch all availabilities for local doctors
  const fetchAllDoctorAvailabilities = async (localDoctors: DoctorResult[]) => {
    const availabilityPromises = localDoctors
      .filter(doctor => doctor.provider === 'LOCAL')
      .map(async doctor => {
        const availability = await fetchDoctorAvailability(doctor.placeId);
        return { doctorId: doctor.placeId, availability };
      });

    const availabilityResults = await Promise.all(availabilityPromises);
    const availabilityMap: { [doctorId: string]: DoctorAvailability[] } = {};

    availabilityResults.forEach(result => {
      availabilityMap[result.doctorId] = result.availability;
    });

    setDoctorAvailabilities(availabilityMap);
  };

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const spec = params.get('spec') || '';
    const radius = parseInt(params.get('radius') || '10', 10);
    const sort = params.get('sort') || 'distance';
    const dir = params.get('dir') || 'asc';
    const openNow = params.get('openNow') === 'true';

    setSearchParams(prev => ({
      ...prev,
      spec,
      radius,
      sort,
      dir,
      openNow,
    }));

    // Set initial radius and sort from URL params
    setRadiusKm(radius);
    setSortBy(sort === 'rating' ? 'rating' : 'distance');

    getUserLocation();
  }, [location.search]);

  const formatDistance = (km: number) => {
    if (km < 1) return `${Math.round(km * 1000)} meters`;
    return `${km.toFixed(1)} km`;
  };

  const formatDuration = (seconds: number) => {
    const mins = Math.round(seconds / 60);
    return `${mins} min${mins !== 1 ? 's' : ''}`;
  };

  // Helper function to format day of week
  const formatDayOfWeek = (day: string): string => {
    const dayMap: { [key: string]: string } = {
      MONDAY: 'Monday',
      TUESDAY: 'Tuesday',
      WEDNESDAY: 'Wednesday',
      THURSDAY: 'Thursday',
      FRIDAY: 'Friday',
      SATURDAY: 'Saturday',
      SUNDAY: 'Sunday',
    };
    return dayMap[day] || day;
  };

  // Helper function to format time
  const formatTime = (time: string): string => {
    try {
      const [hours, minutes] = time.split(':');
      const hour = parseInt(hours, 10);
      const ampm = hour >= 12 ? 'PM' : 'AM';
      const displayHour = hour % 12 || 12;
      return `${displayHour}:${minutes} ${ampm}`;
    } catch (parseError) {
      return time;
    }
  };

  // Helper function to check if doctor is currently open based on availability
  const isDoctorCurrentlyOpen = (availability: DoctorAvailability[]): boolean => {
    const now = new Date();
    const currentDay = now.toLocaleDateString('en-US', { weekday: 'long' }).toUpperCase();
    const currentTime = now.toTimeString().slice(0, 5); // HH:mm format

    const todayAvailability = availability.filter(avail => avail.dayOfWeek === currentDay && avail.isAvailable);

    return todayAvailability.some(avail => {
      return currentTime >= avail.startTime && currentTime <= avail.endTime;
    });
  };

  // Helper function to format availability for display
  const formatAvailabilityForDisplay = (availability: DoctorAvailability[]): string[] => {
    const daysOrder = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];
    const availabilityByDay: { [key: string]: DoctorAvailability[] } = {};

    // Group availability by day
    availability.forEach(avail => {
      if (!availabilityByDay[avail.dayOfWeek]) {
        availabilityByDay[avail.dayOfWeek] = [];
      }
      availabilityByDay[avail.dayOfWeek].push(avail);
    });

    // Format for display
    return daysOrder.map(day => {
      const dayAvailability = availabilityByDay[day];
      if (!dayAvailability || dayAvailability.length === 0) {
        return `${formatDayOfWeek(day)}: Closed`;
      }

      const openSlots = dayAvailability
        .filter(avail => avail.isAvailable)
        .map(avail => `${formatTime(avail.startTime)} - ${formatTime(avail.endTime)}`)
        .join(', ');

      return openSlots ? `${formatDayOfWeek(day)}: ${openSlots}` : `${formatDayOfWeek(day)}: Closed`;
    });
  };

  const handleBackClick = () => {
    navigate(-1);
  };

  const handleRetryLocation = async () => {
    setIsLoading(true);
    setError('');
    setLocationError('');
    setDoctors([]);
    setHasUserLocation(false);

    // Try to get fresh location
    await getUserLocation();
  };

  // Handle sort change
  const handleSortChange = (newSort: 'distance' | 'rating') => {
    setSortBy(newSort);
    if (hasUserLocation && searchParams.lat !== 0 && searchParams.lon !== 0) {
      fetchDoctors(searchParams.lat, searchParams.lon, newSort, radiusKm);
    }
  };

  // Handle radius change
  const handleRadiusChange = (newRadius: number) => {
    setRadiusKm(newRadius);
    if (hasUserLocation && searchParams.lat !== 0 && searchParams.lon !== 0) {
      fetchDoctors(searchParams.lat, searchParams.lon, sortBy, newRadius);
    }
  };

  // Handle viewing doctor profile (only for local doctors)
  const handleViewProfile = async (doctorId: string) => {
    try {
      // Fetch detailed doctor information
      const doctorResponse = await axios.get(`/api/doctor-profiles/${doctorId}`);
      const doctorData = doctorResponse.data;

      setSelectedLocalDoctor(doctorData);
      setShowLocalDoctorDetails(true);

      // Track doctor view history
      if (account?.id) {
        // Get current user's profile
        const userProfileResponse = await axios.get('/api/app-user-profiles');
        const currentUserProfile = userProfileResponse.data.find((profile: any) => profile.internalUser?.id === account.id);

        if (currentUserProfile) {
          const viewHistoryData = {
            viewDate: dayjs().toISOString(),
            user: { id: currentUserProfile.id },
            doctor: { id: doctorId },
          };

          await axios.post('/api/doctor-view-histories', viewHistoryData);
          console.log('Doctor view tracked successfully');
        }
      }
    } catch (err) {
      console.error('Failed to load doctor details or track view:', err);
      // Fallback to navigation if modal fails
      navigate(`/doctor-profile/${doctorId}`);
    }
  };

  const handleGoogleDoctorMoreInfo = async (doctor: DoctorResult) => {
    try {
      // Get detailed information from the existing Google Places API
      const response = await axios.get(`/api/doctors/google/${doctor.placeId}/detail`);
      const detailedDoctor = response.data;

      // Update the selected doctor with detailed information
      setSelectedDoctor({
        ...doctor,
        // Add any additional details from the API response
        ...detailedDoctor,
      });

      // Track the Google Maps doctor view in history using the new endpoint
      if (account?.id) {
        const googleDoctorData = {
          placeId: doctor.placeId,
          doctorName: doctor.name,
          address: doctor.address,
          latitude: doctor.latitude,
          longitude: doctor.longitude,
          rating: doctor.rating,
          userRatingCount: doctor.userRatingCount,
          openNow: doctor.openNow,
          phoneNumber: detailedDoctor.phoneNumber || null,
          website: detailedDoctor.website || null,
          weekdayDescriptions: JSON.stringify(doctor.weekdayDescriptions || []),
        };

        await axios.post('/api/user-history/google-doctor-views', googleDoctorData);
        console.log('Google Maps doctor view tracked successfully');

        // Show success message to user
        alert('Doctor information saved to your viewing history!');
      } else {
        alert('Please log in to save doctor views to your history.');
      }

      console.log('Google doctor details loaded:', detailedDoctor);
    } catch (err) {
      console.error('Failed to load Google doctor details or track view:', err);
      // Still show the modal with basic information if detailed fetch fails
      setSelectedDoctor(doctor);

      // Show error message for tracking failure
      if (account?.id) {
        alert('Failed to save to history. Please try again.');
      }
    }
  };

  const handleShowGoogleDoctorDetails = (doctor: DoctorResult) => {
    setSelectedDoctor(doctor);
    setShowGoogleDoctorDetails(true);
  };

  const handleCloseGoogleDoctorDetails = () => {
    setShowGoogleDoctorDetails(false);
    setSelectedDoctor(null);
  };

  const handleCloseLocalDoctorDetails = () => {
    setShowLocalDoctorDetails(false);
    setSelectedLocalDoctor(null);
  };

  const formatDoctorName = (internalUser: any): string => {
    if (!internalUser) return 'Doctor Profile';

    if (internalUser.firstName && internalUser.lastName) {
      return `Dr. ${internalUser.firstName} ${internalUser.lastName}`;
    } else if (internalUser.firstName) {
      return `Dr. ${internalUser.firstName}`;
    } else if (internalUser.lastName) {
      return `Dr. ${internalUser.lastName}`;
    } else if (internalUser.login) {
      return `Dr. ${internalUser.login}`;
    }
    return 'Doctor Profile';
  };

  return (
    <div className="nearby-doctors-container" style={{ backgroundColor: '#f8f9fa', minHeight: '100vh' }}>
      <Row className="justify-content-center">
        <Col md="12" lg="10" xl="8">
          {/* Enhanced Header Section */}
          <div
            className="header-section mb-4 p-4"
            style={{
              background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
              borderRadius: '15px',
              color: 'white',
              boxShadow: '0 8px 32px rgba(0,0,0,0.1)',
            }}
          >
            <div className="d-flex align-items-center justify-content-between mb-3">
              <Button
                color="light"
                onClick={handleBackClick}
                className="d-flex align-items-center"
                style={{
                  borderRadius: '25px',
                  fontWeight: '500',
                  padding: '8px 16px',
                  boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
                }}
              >
                <i className="fas fa-chevron-left me-2"></i>
                Back to Search
              </Button>

              {hasUserLocation && (
                <div className="d-flex align-items-center">
                  <i className="fas fa-map-marker-alt me-2" style={{ color: '#4CAF50' }}></i>
                  <span className="small">Location Active</span>
                </div>
              )}
            </div>

            <div className="text-center">
              <h1 className="mb-2" style={{ fontWeight: '600', fontSize: '2rem' }}>
                <i className="fas fa-stethoscope me-3"></i>
                Find Nearby Doctors
              </h1>
              <p className="mb-3 opacity-75" style={{ fontSize: '1.1rem' }}>
                {searchParams.spec ? `${searchParams.spec} Specialists` : 'All Specialties'} in your area
              </p>
            </div>
          </div>

          {/* Search Controls */}
          {hasUserLocation && (
            <div className="mb-4">
              <Card className="border-0 shadow-sm" style={{ borderRadius: '15px' }}>
                <CardBody className="p-4">
                  <Row className="align-items-center">
                    <Col md="6" className="mb-3 mb-md-0">
                      <Label className="fw-bold mb-2" style={{ color: '#495057' }}>
                        <i className="fas fa-sort me-2"></i>Sort By
                      </Label>
                      <ButtonGroup className="w-100">
                        <Button
                          color={sortBy === 'distance' ? 'primary' : 'outline-primary'}
                          onClick={() => handleSortChange('distance')}
                          style={{ borderRadius: '20px 0 0 20px', fontWeight: '500' }}
                        >
                          <i className="fas fa-route me-2"></i>Distance
                        </Button>
                        <Button
                          color={sortBy === 'rating' ? 'primary' : 'outline-primary'}
                          onClick={() => handleSortChange('rating')}
                          style={{ borderRadius: '0 20px 20px 0', fontWeight: '500' }}
                        >
                          <i className="fas fa-star me-2"></i>Rating
                        </Button>
                      </ButtonGroup>
                    </Col>
                    <Col md="6">
                      <Label className="fw-bold mb-2" style={{ color: '#495057' }}>
                        <i className="fas fa-search-location me-2"></i>Search Radius: {radiusKm} km
                      </Label>
                      <Input
                        type="range"
                        min="5"
                        max="50"
                        step="5"
                        value={radiusKm}
                        onChange={e => handleRadiusChange(parseInt(e.target.value, 10))}
                        style={{
                          cursor: 'pointer',
                          accentColor: '#667eea',
                        }}
                      />
                      <div className="d-flex justify-content-between mt-1">
                        <small className="text-muted">5 km</small>
                        <small className="text-muted">50 km</small>
                      </div>
                    </Col>
                  </Row>
                </CardBody>
              </Card>
            </div>
          )}

          {/* Enhanced Status Alerts */}
          {gettingLocation && (
            <div className="mb-4">
              <Card className="border-0 shadow-sm" style={{ borderRadius: '15px' }}>
                <CardBody className="text-center py-4">
                  <div className="mb-3">
                    <Spinner color="primary" style={{ width: '3rem', height: '3rem' }} />
                  </div>
                  <h5 className="text-primary mb-2">
                    <i className="fas fa-satellite-dish me-2"></i>
                    Detecting Your Location
                  </h5>
                  <p className="text-muted mb-0">Please allow location access to find nearby doctors...</p>
                </CardBody>
              </Card>
            </div>
          )}

          {locationError && (
            <div className="mb-4">
              <Card className="border-0 shadow-sm" style={{ borderRadius: '15px', borderLeft: '4px solid #dc3545' }}>
                <CardBody>
                  <div className="d-flex justify-content-between align-items-start">
                    <div className="flex-grow-1">
                      <h5 className="text-danger mb-2">
                        <i className="fas fa-exclamation-triangle me-2"></i>
                        Location Access Issue
                      </h5>
                      <p className="text-muted mb-0">{locationError}</p>
                    </div>
                    <Button color="danger" size="sm" onClick={handleRetryLocation} style={{ borderRadius: '20px' }}>
                      <i className="fas fa-redo me-1"></i>
                      Try Again
                    </Button>
                  </div>
                </CardBody>
              </Card>
            </div>
          )}

          {!hasUserLocation && !gettingLocation ? (
            <div className="mb-4">
              <Card
                className="border-0 shadow-sm text-center"
                style={{
                  borderRadius: '15px',
                  background: 'linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)',
                }}
              >
                <CardBody className="py-5">
                  <div className="mb-4">
                    <i
                      className="fas fa-map-marker-alt"
                      style={{
                        fontSize: '4rem',
                        color: '#ff6b6b',
                        opacity: 0.8,
                      }}
                    ></i>
                  </div>
                  <h3 className="mb-3" style={{ color: '#2c3e50', fontWeight: '600' }}>
                    Location Required
                  </h3>
                  <p className="mb-4 text-muted" style={{ fontSize: '1.1rem' }}>
                    We need your location to find nearby doctors and provide accurate distances.
                  </p>
                  <Button
                    color="primary"
                    size="lg"
                    onClick={handleRetryLocation}
                    style={{
                      borderRadius: '25px',
                      padding: '12px 30px',
                      fontWeight: '500',
                      boxShadow: '0 4px 15px rgba(0,123,255,0.3)',
                    }}
                  >
                    <i className="fas fa-crosshairs me-2"></i>
                    Allow Location Access
                  </Button>
                </CardBody>
              </Card>
            </div>
          ) : isLoading ? (
            <div className="mb-4">
              <Card className="border-0 shadow-sm" style={{ borderRadius: '15px' }}>
                <CardBody className="text-center py-5">
                  <div className="mb-4">
                    <div className="d-flex justify-content-center">
                      <Spinner color="primary" style={{ width: '3rem', height: '3rem' }} />
                    </div>
                  </div>
                  <h4 className="text-primary mb-2">
                    <i className="fas fa-search me-2"></i>
                    Searching for Doctors
                  </h4>
                  <p className="text-muted mb-0">Finding the best doctors near you...</p>
                  <div className="mt-3">
                    <div className="progress" style={{ height: '4px', borderRadius: '2px' }}>
                      <div className="progress-bar progress-bar-striped progress-bar-animated" style={{ width: '100%' }}></div>
                    </div>
                  </div>
                </CardBody>
              </Card>
            </div>
          ) : error ? (
            <div className="mb-4">
              <Card className="border-0 shadow-sm" style={{ borderRadius: '15px', borderLeft: '4px solid #dc3545' }}>
                <CardBody>
                  <h5 className="text-danger mb-2">
                    <i className="fas fa-exclamation-circle me-2"></i>
                    Search Error
                  </h5>
                  <p className="text-muted mb-0">{error}</p>
                </CardBody>
              </Card>
            </div>
          ) : (!Array.isArray(doctors) || doctors.length === 0) && hasUserLocation ? (
            <div className="mb-4">
              <Card
                className="border-0 shadow-sm text-center"
                style={{
                  borderRadius: '15px',
                  background: 'linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%)',
                }}
              >
                <CardBody className="py-5">
                  <div className="mb-4">
                    <i
                      className="fas fa-search"
                      style={{
                        fontSize: '4rem',
                        color: '#2196f3',
                        opacity: 0.6,
                      }}
                    ></i>
                  </div>
                  <h4 className="mb-3" style={{ color: '#1976d2', fontWeight: '600' }}>
                    No Doctors Found
                  </h4>
                  <p className="text-muted mb-4" style={{ fontSize: '1.1rem' }}>
                    No doctors found matching your criteria in this area.
                  </p>
                  <div className="d-flex flex-column flex-sm-row gap-2 justify-content-center">
                    <Button
                      color="outline-primary"
                      style={{ borderRadius: '20px' }}
                      onClick={() => {
                        const newRadius = Math.min(radiusKm + 10, 50);
                        handleRadiusChange(newRadius);
                      }}
                    >
                      <i className="fas fa-expand-arrows-alt me-2"></i>
                      Expand Search Area
                    </Button>
                    <Button color="outline-secondary" style={{ borderRadius: '20px' }} onClick={handleRetryLocation}>
                      <i className="fas fa-redo me-2"></i>
                      Try Again
                    </Button>
                  </div>
                </CardBody>
              </Card>
            </div>
          ) : hasUserLocation && Array.isArray(doctors) ? (
            <div className="doctors-list">
              {/* Results Summary */}
              <div className="mb-4">
                <Card className="border-0 shadow-sm" style={{ borderRadius: '15px', backgroundColor: '#f8f9fa' }}>
                  <CardBody className="py-3">
                    <div className="d-flex align-items-center justify-content-between">
                      <div className="d-flex align-items-center">
                        <i className="fas fa-list-ul text-primary me-2"></i>
                        <span className="fw-bold text-dark">
                          Found {doctors.length} doctor{doctors.length !== 1 ? 's' : ''}
                        </span>
                      </div>
                      <div className="d-flex align-items-center text-muted">
                        <i className="fas fa-map-marker-alt me-1"></i>
                        <small>Within {radiusKm} km</small>
                      </div>
                    </div>
                  </CardBody>
                </Card>
              </div>

              {doctors.map((doctor, index) => (
                <Card
                  key={index}
                  className="mb-4 doctor-card border-0 shadow-sm"
                  style={{
                    borderRadius: '20px',
                    transition: 'all 0.3s ease',
                    cursor: 'pointer',
                    border: '1px solid rgba(0,0,0,0.05) !important',
                  }}
                  onMouseEnter={e => {
                    e.currentTarget.style.transform = 'translateY(-5px)';
                    e.currentTarget.style.boxShadow = '0 10px 30px rgba(0,0,0,0.15)';
                  }}
                  onMouseLeave={e => {
                    e.currentTarget.style.transform = 'translateY(0)';
                    e.currentTarget.style.boxShadow = '0 2px 10px rgba(0,0,0,0.1)';
                  }}
                >
                  <CardBody className="p-4">
                    {/* Doctor Header */}
                    <div className="d-flex justify-content-between align-items-start mb-3">
                      <div className="flex-grow-1">
                        <div className="d-flex align-items-center mb-2">
                          <div
                            className="me-3 d-flex align-items-center justify-content-center"
                            style={{
                              width: '50px',
                              height: '50px',
                              borderRadius: '50%',
                              backgroundColor: doctor.provider === 'LOCAL' ? '#e3f2fd' : '#f3e5f5',
                              color: doctor.provider === 'LOCAL' ? '#1976d2' : '#7b1fa2',
                            }}
                          >
                            <i
                              className={doctor.provider === 'LOCAL' ? 'fas fa-user-md' : 'fab fa-google'}
                              style={{ fontSize: '1.5rem' }}
                            ></i>
                          </div>
                          <div>
                            <h4 className="mb-1" style={{ fontWeight: '600', color: '#2c3e50' }}>
                              {doctor.name}
                            </h4>
                            <Badge
                              color={doctor.provider === 'LOCAL' ? 'primary' : 'info'}
                              className="px-3 py-1"
                              style={{ borderRadius: '15px', fontSize: '0.75rem', fontWeight: '500' }}
                            >
                              {doctor.provider === 'LOCAL' ? (
                                <>
                                  <i className="fas fa-hospital me-1"></i>
                                  Our Network
                                </>
                              ) : (
                                <>
                                  <i className="fab fa-google me-1"></i>
                                  Google Maps
                                </>
                              )}
                            </Badge>
                          </div>
                        </div>
                      </div>
                      <div className="d-flex flex-column align-items-end">
                        {doctor.provider === 'GOOGLE' && (
                          <div className="mb-2">
                            {doctor.openNow ? (
                              <Badge color="success" className="px-3 py-1 mb-1" style={{ borderRadius: '15px', fontSize: '0.75rem' }}>
                                <i className="fas fa-clock me-1"></i>
                                Open Now
                              </Badge>
                            ) : (
                              <Badge color="secondary" className="px-3 py-1 mb-1" style={{ borderRadius: '15px', fontSize: '0.75rem' }}>
                                <i className="fas fa-clock me-1"></i>
                                Closed
                              </Badge>
                            )}
                            {doctor.rating > 0 && (
                              <div>
                                <Badge color="warning" className="px-3 py-1" style={{ borderRadius: '15px', fontSize: '0.75rem' }}>
                                  <i className="fas fa-star me-1"></i>
                                  {doctor.rating} ({doctor.userRatingCount})
                                </Badge>
                              </div>
                            )}
                          </div>
                        )}
                        {doctor.provider === 'LOCAL' && (
                          <div className="d-flex flex-column align-items-end">
                            <Badge color="success" className="px-3 py-1 mb-1" style={{ borderRadius: '15px', fontSize: '0.75rem' }}>
                              <i className="fas fa-check-circle me-1"></i>
                              Verified
                            </Badge>
                            {doctorAvailabilities[doctor.placeId] && doctorAvailabilities[doctor.placeId].length > 0 && (
                              <Badge
                                color={isDoctorCurrentlyOpen(doctorAvailabilities[doctor.placeId]) ? 'success' : 'secondary'}
                                className="px-3 py-1"
                                style={{ borderRadius: '15px', fontSize: '0.75rem' }}
                              >
                                <i className="fas fa-clock me-1"></i>
                                {isDoctorCurrentlyOpen(doctorAvailabilities[doctor.placeId]) ? 'Open Now' : 'Closed'}
                              </Badge>
                            )}
                          </div>
                        )}
                      </div>
                    </div>

                    {/* Address */}
                    <div className="mb-3">
                      <div className="d-flex align-items-start">
                        <i className="fas fa-map-marker-alt text-muted me-2 mt-1"></i>
                        <span className="text-muted" style={{ lineHeight: '1.5' }}>
                          {doctor.address}
                        </span>
                      </div>
                    </div>

                    {/* Distance and Travel Info */}
                    <div className="d-flex flex-wrap gap-2 mb-3">
                      <div
                        className="d-flex align-items-center px-3 py-2"
                        style={{
                          backgroundColor: '#e8f5e8',
                          borderRadius: '15px',
                          border: '1px solid #c8e6c9',
                        }}
                      >
                        <i className="fas fa-route text-success me-2"></i>
                        <span className="text-success fw-bold" style={{ fontSize: '0.9rem' }}>
                          {formatDistance(doctor.distanceKm)}
                        </span>
                      </div>
                      {doctor.travelDurationSec && (
                        <div
                          className="d-flex align-items-center px-3 py-2"
                          style={{
                            backgroundColor: '#e3f2fd',
                            borderRadius: '15px',
                            border: '1px solid #bbdefb',
                          }}
                        >
                          <i className="fas fa-car text-primary me-2"></i>
                          <span className="text-primary fw-bold" style={{ fontSize: '0.9rem' }}>
                            ~{formatDuration(doctor.travelDurationSec)}
                          </span>
                        </div>
                      )}
                    </div>

                    {/* Opening Hours for Google doctors */}
                    {doctor.provider === 'GOOGLE' && doctor.weekdayDescriptions && doctor.weekdayDescriptions.length > 0 && (
                      <div className="opening-hours mb-4">
                        <div
                          className="p-3"
                          style={{
                            backgroundColor: '#f8f9fa',
                            borderRadius: '15px',
                            border: '1px solid #e9ecef',
                          }}
                        >
                          <h6 className="mb-3 d-flex align-items-center" style={{ color: '#495057', fontWeight: '600' }}>
                            <i className="fas fa-clock text-primary me-2"></i>
                            Opening Hours
                          </h6>
                          <div className="row">
                            {doctor.weekdayDescriptions.map((desc, i) => (
                              <div key={i} className="col-12 mb-1">
                                <small
                                  className={`d-block ${desc.includes('Closed') ? 'text-muted' : 'text-dark'}`}
                                  style={{ fontSize: '0.85rem', lineHeight: '1.4' }}
                                >
                                  {desc}
                                </small>
                              </div>
                            ))}
                          </div>
                        </div>
                      </div>
                    )}

                    {/* Availability Hours for Local doctors */}
                    {doctor.provider === 'LOCAL' &&
                      doctorAvailabilities[doctor.placeId] &&
                      doctorAvailabilities[doctor.placeId].length > 0 && (
                        <div className="availability-hours mb-4">
                          <div
                            className="p-3"
                            style={{
                              backgroundColor: '#f0f8ff',
                              borderRadius: '15px',
                              border: '1px solid #cce7ff',
                            }}
                          >
                            <h6 className="mb-3 d-flex align-items-center" style={{ color: '#495057', fontWeight: '600' }}>
                              <i className="fas fa-calendar-check text-primary me-2"></i>
                              Availability Schedule
                            </h6>
                            <div className="row">
                              {formatAvailabilityForDisplay(doctorAvailabilities[doctor.placeId]).map((schedule, i) => (
                                <div key={i} className="col-12 mb-1">
                                  <small
                                    className={`d-block ${schedule.includes('Closed') ? 'text-muted' : 'text-dark'}`}
                                    style={{ fontSize: '0.85rem', lineHeight: '1.4' }}
                                  >
                                    {schedule}
                                  </small>
                                </div>
                              ))}
                            </div>
                          </div>
                        </div>
                      )}

                    {/* Action Buttons */}
                    <div className="d-flex flex-column flex-sm-row gap-2 mt-4">
                      {doctor.provider === 'LOCAL' ? (
                        <>
                          <Button
                            color="primary"
                            className="flex-fill"
                            onClick={() => handleViewProfile(doctor.placeId)}
                            style={{
                              borderRadius: '25px',
                              fontWeight: '500',
                              padding: '12px 20px',
                            }}
                          >
                            <i className="fas fa-user-md me-2"></i>
                            View Profile
                          </Button>
                          <Button
                            color="outline-primary"
                            className="flex-fill"
                            href={`https://www.google.com/maps/search/?api=1&query=${doctor.latitude},${doctor.longitude}`}
                            target="_blank"
                            rel="noopener noreferrer"
                            style={{
                              borderRadius: '25px',
                              fontWeight: '500',
                              padding: '12px 20px',
                            }}
                          >
                            <i className="fas fa-directions me-2"></i>
                            Get Directions
                          </Button>
                        </>
                      ) : (
                        <>
                          <Button
                            color="primary"
                            className="flex-fill"
                            href={`https://www.google.com/maps/search/?api=1&query=${doctor.latitude},${doctor.longitude}&query_place_id=${doctor.placeId}`}
                            target="_blank"
                            rel="noopener noreferrer"
                            style={{
                              borderRadius: '25px',
                              fontWeight: '500',
                              padding: '12px 20px',
                            }}
                          >
                            <i className="fas fa-directions me-2"></i>
                            Get Directions
                          </Button>
                          <Button
                            color="outline-secondary"
                            className="flex-fill"
                            onClick={() => handleShowGoogleDoctorDetails(doctor)}
                            style={{
                              borderRadius: '25px',
                              fontWeight: '500',
                              padding: '12px 20px',
                            }}
                          >
                            <i className="fas fa-info-circle me-2"></i>
                            More Info
                          </Button>
                        </>
                      )}
                    </div>
                  </CardBody>
                </Card>
              ))}
            </div>
          ) : null}

          {/* Add some spacing at the bottom */}
          <div style={{ height: '50px' }}></div>
        </Col>
      </Row>

      {/* Google Doctor Details Modal */}
      {showGoogleDoctorDetails && selectedDoctor && (
        <Modal isOpen={showGoogleDoctorDetails} toggle={handleCloseGoogleDoctorDetails} size="lg">
          <ModalHeader toggle={handleCloseGoogleDoctorDetails}>
            <div className="d-flex align-items-center">
              <i className="fab fa-google text-primary me-2"></i>
              Google Maps Doctor Details
            </div>
          </ModalHeader>
          <ModalBody>
            <div className="google-doctor-details">
              {/* Doctor Header */}
              <div className="text-center mb-4">
                <div
                  className="mx-auto mb-3 d-flex align-items-center justify-content-center"
                  style={{
                    width: '80px',
                    height: '80px',
                    borderRadius: '50%',
                    backgroundColor: '#f3e5f5',
                    color: '#7b1fa2',
                  }}
                >
                  <i className="fas fa-user-md" style={{ fontSize: '2rem' }}></i>
                </div>
                <h4 className="mb-2">{selectedDoctor.name}</h4>
                <Badge color="info" className="px-3 py-2" style={{ borderRadius: '15px' }}>
                  <i className="fab fa-google me-1"></i>
                  Google Maps Provider
                </Badge>
              </div>

              {/* Status and Rating */}
              <div className="d-flex justify-content-center gap-3 mb-4">
                {selectedDoctor.openNow ? (
                  <Badge color="success" className="px-3 py-2" style={{ borderRadius: '15px' }}>
                    <i className="fas fa-clock me-1"></i>
                    Open Now
                  </Badge>
                ) : (
                  <Badge color="secondary" className="px-3 py-2" style={{ borderRadius: '15px' }}>
                    <i className="fas fa-clock me-1"></i>
                    Closed
                  </Badge>
                )}
                {selectedDoctor.rating > 0 && (
                  <Badge color="warning" className="px-3 py-2" style={{ borderRadius: '15px' }}>
                    <i className="fas fa-star me-1"></i>
                    {selectedDoctor.rating}/5 ({selectedDoctor.userRatingCount} reviews)
                  </Badge>
                )}
              </div>

              {/* Location Information */}
              <Card className="mb-4 border-0" style={{ backgroundColor: '#f8f9fa' }}>
                <CardBody>
                  <h6 className="mb-3">
                    <i className="fas fa-map-marker-alt text-primary me-2"></i>
                    Location & Distance
                  </h6>
                  <div className="mb-2">
                    <strong>Address:</strong>
                    <div className="text-muted">{selectedDoctor.address}</div>
                  </div>
                  <div className="d-flex gap-3 mt-3">
                    <div className="d-flex align-items-center">
                      <i className="fas fa-route text-success me-2"></i>
                      <span className="fw-bold text-success">{formatDistance(selectedDoctor.distanceKm)}</span>
                    </div>
                    {selectedDoctor.travelDurationSec && (
                      <div className="d-flex align-items-center">
                        <i className="fas fa-car text-primary me-2"></i>
                        <span className="fw-bold text-primary">~{formatDuration(selectedDoctor.travelDurationSec)}</span>
                      </div>
                    )}
                  </div>
                </CardBody>
              </Card>

              {/* Opening Hours */}
              {selectedDoctor.weekdayDescriptions && selectedDoctor.weekdayDescriptions.length > 0 && (
                <Card className="mb-4 border-0" style={{ backgroundColor: '#f8f9fa' }}>
                  <CardBody>
                    <h6 className="mb-3">
                      <i className="fas fa-clock text-primary me-2"></i>
                      Opening Hours
                    </h6>
                    <div className="row">
                      {selectedDoctor.weekdayDescriptions.map((desc, i) => (
                        <div key={i} className="col-12 mb-1">
                          <small
                            className={`d-block ${desc.includes('Closed') ? 'text-muted' : 'text-dark'}`}
                            style={{ fontSize: '0.9rem', lineHeight: '1.4' }}
                          >
                            {desc}
                          </small>
                        </div>
                      ))}
                    </div>
                  </CardBody>
                </Card>
              )}

              {/* Actions */}
              <div className="d-flex gap-2">
                <Button
                  color="primary"
                  className="flex-fill"
                  href={`https://www.google.com/maps/search/?api=1&query=${selectedDoctor.latitude},${selectedDoctor.longitude}&query_place_id=${selectedDoctor.placeId}`}
                  target="_blank"
                  rel="noopener noreferrer"
                  style={{ borderRadius: '25px' }}
                >
                  <i className="fas fa-directions me-2"></i>
                  Get Directions
                </Button>
                <Button
                  color="outline-primary"
                  className="flex-fill"
                  onClick={() => handleGoogleDoctorMoreInfo(selectedDoctor)}
                  style={{ borderRadius: '25px' }}
                >
                  <i className="fas fa-history me-2"></i>
                  Save to History
                </Button>
              </div>
            </div>
          </ModalBody>
          <ModalFooter>
            <Button color="secondary" onClick={handleCloseGoogleDoctorDetails}>
              Close
            </Button>
          </ModalFooter>
        </Modal>
      )}

      {/* Local Doctor Details Modal */}
      {showLocalDoctorDetails && selectedLocalDoctor && (
        <Modal isOpen={showLocalDoctorDetails} toggle={handleCloseLocalDoctorDetails} size="lg">
          <ModalHeader toggle={handleCloseLocalDoctorDetails}>
            <div className="d-flex align-items-center">
              <i className="fas fa-user-md text-primary me-2"></i>
              Doctor Profile Details
            </div>
          </ModalHeader>
          <ModalBody>
            <div className="local-doctor-details">
              {/* Doctor Header */}
              <div className="text-center mb-4">
                <div
                  className="mx-auto mb-3 d-flex align-items-center justify-content-center"
                  style={{
                    width: '80px',
                    height: '80px',
                    borderRadius: '50%',
                    backgroundColor: '#e3f2fd',
                    color: '#1976d2',
                  }}
                >
                  <i className="fas fa-user-md" style={{ fontSize: '2rem' }}></i>
                </div>
                <h4 className="mb-2">{formatDoctorName(selectedLocalDoctor.internalUser)}</h4>
                <Badge color="primary" className="px-3 py-2" style={{ borderRadius: '15px' }}>
                  <i className="fas fa-hospital me-1"></i>
                  Our Network Doctor
                </Badge>
              </div>

              {/* Verification Status */}
              <div className="d-flex justify-content-center gap-3 mb-4">
                <Badge
                  color={selectedLocalDoctor.isVerified ? 'success' : 'warning'}
                  className="px-3 py-2"
                  style={{ borderRadius: '15px' }}
                >
                  <i className={`fas ${selectedLocalDoctor.isVerified ? 'fa-check-circle' : 'fa-clock'} me-1`}></i>
                  {selectedLocalDoctor.isVerified ? 'Verified Doctor' : 'Pending Verification'}
                </Badge>
              </div>

              {/* Specializations */}
              {selectedLocalDoctor.specializations && selectedLocalDoctor.specializations.length > 0 && (
                <Card className="mb-4 border-0" style={{ backgroundColor: '#f8f9fa' }}>
                  <CardBody>
                    <h6 className="mb-3">
                      <i className="fas fa-stethoscope text-primary me-2"></i>
                      Specializations
                    </h6>
                    <div className="d-flex flex-wrap gap-2">
                      {selectedLocalDoctor.specializations.map((spec: any, index: number) => (
                        <Badge key={index} color="primary" pill className="px-3 py-2">
                          {spec.name}
                        </Badge>
                      ))}
                    </div>
                  </CardBody>
                </Card>
              )}

              {/* Contact Information */}
              <Card className="mb-4 border-0" style={{ backgroundColor: '#f8f9fa' }}>
                <CardBody>
                  <h6 className="mb-3">
                    <i className="fas fa-address-book text-primary me-2"></i>
                    Contact Information
                  </h6>

                  {/* Phone */}
                  {selectedLocalDoctor.phoneNumber && selectedLocalDoctor.phoneNumber !== 'N/A' && (
                    <div className="mb-2">
                      <strong>Phone:</strong>
                      <div className="text-muted">
                        <i className="fas fa-phone me-2"></i>
                        {selectedLocalDoctor.phoneNumber}
                      </div>
                    </div>
                  )}

                  {/* Email */}
                  {selectedLocalDoctor.internalUser?.email && (
                    <div className="mb-2">
                      <strong>Email:</strong>
                      <div className="text-muted">
                        <i className="fas fa-envelope me-2"></i>
                        {selectedLocalDoctor.internalUser.email}
                      </div>
                    </div>
                  )}

                  {/* Office Address */}
                  {selectedLocalDoctor.officeAddress && (
                    <div>
                      <strong>Office Address:</strong>
                      <div className="text-muted">
                        <i className="fas fa-map-marker-alt me-2"></i>
                        {selectedLocalDoctor.officeAddress}
                      </div>
                    </div>
                  )}
                </CardBody>
              </Card>

              {/* Professional Information */}
              <Card className="mb-4 border-0" style={{ backgroundColor: '#f8f9fa' }}>
                <CardBody>
                  <h6 className="mb-3">
                    <i className="fas fa-id-card text-primary me-2"></i>
                    Professional Information
                  </h6>

                  {selectedLocalDoctor.inpeCode && (
                    <div className="mb-2">
                      <strong>INPE Code:</strong>
                      <div className="text-muted">{selectedLocalDoctor.inpeCode}</div>
                    </div>
                  )}

                  <div>
                    <strong>Profile ID:</strong>
                    <div className="text-muted">{selectedLocalDoctor.id}</div>
                  </div>
                </CardBody>
              </Card>

              {/* Actions */}
              <div className="d-flex gap-2">
                <Button
                  color="outline-primary"
                  className="flex-fill"
                  href={`https://www.google.com/maps/search/?api=1&query=${selectedLocalDoctor.latitude},${selectedLocalDoctor.longitude}`}
                  target="_blank"
                  rel="noopener noreferrer"
                  style={{ borderRadius: '25px' }}
                >
                  <i className="fas fa-directions me-2"></i>
                  Get Directions
                </Button>
              </div>
            </div>
          </ModalBody>
          <ModalFooter>
            <Button color="secondary" onClick={handleCloseLocalDoctorDetails}>
              Close
            </Button>
          </ModalFooter>
        </Modal>
      )}
    </div>
  );
};

export default NearbyDoctors;
