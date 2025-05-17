import './nearby-doctors.scss';
import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Alert, Col, Row, Button, Card, CardBody, CardText, Badge, Spinner } from 'reactstrap';
import axios from 'axios';

interface DoctorResult {
  provider: string;
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
}

export const NearbyDoctors = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [doctors, setDoctors] = useState<DoctorResult[]>([]); // Initialize as empty array instead of null
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

    const getUserLocation = () => {
      setGettingLocation(true);
      setLocationError('');

      if (!navigator.geolocation) {
        setLocationError('Geolocation is not supported by your browser');
        setGettingLocation(false);
        fetchDoctors(50.0, 30.0); // Default coordinates
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
          fetchDoctors(latitude, longitude);
          setGettingLocation(false);
        },
        err => {
          setLocationError('Unable to retrieve your location. Using default coordinates.');
          console.error('Geolocation error:', err);
          fetchDoctors(50.0, 30.0); // Default coordinates
          setGettingLocation(false);
        },
        {
          enableHighAccuracy: true,
          timeout: 10000,
          maximumAge: 0,
        },
      );
    };

    const fetchDoctors = async (lat: number, lon: number) => {
      setIsLoading(true);
      setError('');

      try {
        const response = await axios.get('/api/doctors/google/nearby', {
          params: {
            spec,
            lat,
            lon,
            radius,
            sort,
            dir,
            openNow,
          },
        });
        setDoctors(response.data || []); // Ensure we always set an array
      } catch (err) {
        setError('Failed to fetch doctors. Please try again later.');
        console.error('API Error:', err);
        setDoctors([]); // Set empty array on error
      } finally {
        setIsLoading(false);
      }
    };

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

  const handleBackClick = () => {
    navigate(-1);
  };

  const handleRetryLocation = () => {
    setIsLoading(true);
    setError('');
    setLocationError('');
    setDoctors([]);

    const params = new URLSearchParams(location.search);
    const spec = params.get('spec') || '';
    const radius = parseInt(params.get('radius') || '10', 10);
    const sort = params.get('sort') || 'distance';
    const dir = params.get('dir') || 'asc';
    const openNow = params.get('openNow') === 'true';

    setGettingLocation(true);
    navigator.geolocation.getCurrentPosition(
      position => {
        const { latitude, longitude } = position.coords;
        setSearchParams(prev => ({
          ...prev,
          lat: latitude,
          lon: longitude,
        }));
        fetchDoctors(latitude, longitude);
        setGettingLocation(false);
      },
      err => {
        setLocationError('Failed to get location again. Using default coordinates.');
        console.error('Geolocation error:', err);
        fetchDoctors(50.0, 30.0); // Default coordinates
        setGettingLocation(false);
      },
    );
  };

  const fetchDoctors = async (lat: number, lon: number) => {
    setIsLoading(true);
    setError('');

    try {
      const response = await axios.get('/api/doctors/google/nearby', {
        params: {
          spec: searchParams.spec,
          lat,
          lon,
          radius: searchParams.radius,
          sort: searchParams.sort,
          dir: searchParams.dir,
          openNow: searchParams.openNow,
        },
      });
      setDoctors(response.data || []);
    } catch (err) {
      setError('Failed to fetch doctors. Please try again later.');
      console.error('API Error:', err);
      setDoctors([]);
    } finally {
      setIsLoading(false);
    }
  };
  return (
    <div className="nearby-doctors-container">
      <Row className="justify-content-center">
        <Col md="10" lg="8">
          <div className="header-section mb-4">
            <Button color="light" onClick={handleBackClick} className="mb-3">
              <i className="fas fa-arrow-left me-2"></i> Back
            </Button>
            <h2 className="mb-3">
              <i className="fas fa-map-marker-alt me-2 text-primary"></i>
              Nearby {searchParams.spec} Specialists
            </h2>
            <div className="search-params mb-4">
              <Badge color="info" className="me-2">
                Radius: {searchParams.radius} km
              </Badge>
              <Badge color="info" className="me-2">
                Sort: {searchParams.sort} ({searchParams.dir})
              </Badge>
              {searchParams.openNow && (
                <Badge color="success">
                  <i className="fas fa-clock me-1"></i> Open Now
                </Badge>
              )}
            </div>
          </div>

          {gettingLocation && (
            <Alert color="info" className="d-flex align-items-center">
              <Spinner size="sm" className="me-2" />
              Getting your current location...
            </Alert>
          )}

          {locationError && (
            <Alert color="warning" className="d-flex justify-content-between align-items-center">
              <div>
                <i className="fas fa-exclamation-triangle me-2"></i>
                {locationError}
              </div>
              <Button color="warning" size="sm" onClick={handleRetryLocation}>
                Retry
              </Button>
            </Alert>
          )}

          {isLoading ? (
            <div className="text-center py-5">
              <Spinner color="primary" />
              <p className="mt-3">Finding nearby doctors...</p>
            </div>
          ) : error ? (
            <Alert color="danger">{error}</Alert>
          ) : doctors.length === 0 ? (
            <Alert color="warning">No doctors found matching your criteria. Try expanding your search radius.</Alert>
          ) : (
            <div className="doctors-list">
              {doctors.map((doctor, index) => (
                <Card key={index} className="mb-4 shadow-sm doctor-card">
                  <CardBody>
                    <div className="d-flex justify-content-between align-items-start mb-2">
                      <h4 className="mb-0">{doctor.name}</h4>
                      <div>
                        {doctor.openNow ? (
                          <Badge color="success" pill className="me-2">
                            Open Now
                          </Badge>
                        ) : (
                          <Badge color="secondary" pill className="me-2">
                            Closed
                          </Badge>
                        )}
                        <Badge color="warning" pill>
                          <i className="fas fa-star me-1"></i> {doctor.rating} ({doctor.userRatingCount})
                        </Badge>
                      </div>
                    </div>
                    <CardText className="text-muted mb-2">
                      <i className="fas fa-map-marker-alt me-2"></i>
                      {doctor.address}
                    </CardText>
                    <div className="d-flex flex-wrap gap-2 mb-3">
                      <Badge color="light" className="text-dark">
                        <i className="fas fa-road me-1"></i> {formatDistance(doctor.distanceKm)}
                      </Badge>
                      <Badge color="light" className="text-dark">
                        <i className="fas fa-car me-1"></i> ~{formatDuration(doctor.travelDurationSec)}
                      </Badge>
                    </div>
                    <div className="opening-hours">
                      <h6 className="mb-2">Opening Hours:</h6>
                      <ul className="list-unstyled">
                        {(doctor.weekdayDescriptions || []).map((desc, i) => (
                          <li key={i} className={desc.includes('Closed') ? 'text-muted' : ''}>
                            {desc}
                          </li>
                        ))}
                      </ul>
                    </div>
                    <Button
                      color="primary"
                      href={`https://www.google.com/maps/search/?api=1&query=${doctor.latitude},${doctor.longitude}&query_place_id=${doctor.placeId}`}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="mt-2"
                    >
                      <i className="fas fa-directions me-2"></i> Get Directions
                    </Button>
                  </CardBody>
                </Card>
              ))}
            </div>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default NearbyDoctors;
