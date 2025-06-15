import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { Button, Alert, Card, Form, FormGroup, Label, Input, Spinner } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { Translate } from 'react-jhipster';
import { useAppDispatch } from 'app/config/store';
import { completeRoleSelection } from 'app/shared/reducers/authentication';
import { fetchProfileStatus, setDoctorProfileComplete, setHasDoctorProfile } from 'app/shared/reducers/profile';
import { AUTHORITIES } from 'app/config/constants';
import './complete-doctor-profile.scss';

const CompleteDoctorProfile = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  /* form fields */
  const [inpeCode, setInpeCode] = useState('');
  const [latitude, setLatitude] = useState<number | null>(null);
  const [longitude, setLongitude] = useState<number | null>(null);
  const [officeAddress, setOfficeAddress] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [locationLoading, setLocationLoading] = useState(false);
  const [locationError, setLocationError] = useState<string | null>(null);

  // Get user's location on component mount
  useEffect(() => {
    getCurrentLocation();
  }, []);

  const getCurrentLocation = () => {
    if (!navigator.geolocation) {
      setLocationError('Geolocation is not supported by this browser');
      return;
    }

    setLocationLoading(true);
    setLocationError(null);

    navigator.geolocation.getCurrentPosition(
      position => {
        setLatitude(position.coords.latitude);
        setLongitude(position.coords.longitude);
        setLocationLoading(false);

        // Optionally get address from coordinates
        getAddressFromCoordinates(position.coords.latitude, position.coords.longitude);
      },
      geoError => {
        setLocationLoading(false);
        switch (geoError.code) {
          case geoError.PERMISSION_DENIED:
            setLocationError('Location access denied by user');
            break;
          case geoError.POSITION_UNAVAILABLE:
            setLocationError('Location information is unavailable');
            break;
          case geoError.TIMEOUT:
            setLocationError('Location request timed out');
            break;
          default:
            setLocationError('An unknown error occurred while retrieving location');
            break;
        }
      },
      {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 60000,
      },
    );
  };

  const getAddressFromCoordinates = async (lat: number, lng: number) => {
    try {
      // Using a free geocoding service (you might want to use Google Maps API or similar)
      const response = await fetch(
        `https://api.bigdatacloud.net/data/reverse-geocode-client?latitude=${lat}&longitude=${lng}&localityLanguage=en`,
      );
      const data = await response.json();

      if (data && data.locality && data.countryName) {
        const address = `${data.locality}, ${data.principalSubdivision || ''}, ${data.countryName}`.replace(', ,', ',');
        setOfficeAddress(address);
      }
    } catch (geocodeError) {
      console.warn('Could not get address from coordinates:', geocodeError);
      // Don't show error to user as this is optional
    }
  };

  const validateForm = () => {
    if (!inpeCode.trim()) {
      setError('INPE Code is required');
      return false;
    }
    if (!phoneNumber.trim()) {
      setError('Phone number is required');
      return false;
    }
    if (!officeAddress.trim()) {
      setError('Office address is required');
      return false;
    }
    if (latitude === null || longitude === null) {
      setError('Location coordinates are required. Please allow location access or enter manually.');
      return false;
    }
    return true;
  };

  const submit = async () => {
    if (!validateForm()) return;

    setSaving(true);
    setError(null);

    const payload = {
      type: 'DOCTOR',
      inpeCode: inpeCode.trim(),
      officeAddress: officeAddress.trim(),
      phoneNumber: phoneNumber.trim(),
      latitude,
      longitude,
    };

    try {
      /* 1) send to backend */
      await axios.post('/api/profile/choose-type', payload);

      /* 2) optimistic flags so redirect logic is happy */
      dispatch(setHasDoctorProfile(true));
      dispatch(setDoctorProfileComplete(true));
      dispatch(completeRoleSelection(AUTHORITIES.DOCTOR));

      /* 3) fetch status (no getSession – avoids overwrite lag) */
      await dispatch(fetchProfileStatus());

      /* 4) home */
      navigate('/', { replace: true });
    } catch (e: any) {
      setError(e.response?.data?.message || 'Profile creation failed. Please try again.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="complete-doctor-profile-container">
      <div className="complete-doctor-profile-content">
        <Card className="profile-form-card">
          <div className="card-header">
            <div className="header-icon">
              <FontAwesomeIcon icon="user-md" />
            </div>
            <h2 className="header-title">
              <Translate contentKey="CompleteDoctorProfile.title">Complete Doctor Profile</Translate>
            </h2>
            <p className="header-subtitle">Please provide your professional information to complete your doctor profile</p>
          </div>

          <div className="card-body">
            {error && (
              <Alert color="danger" className="form-alert">
                <FontAwesomeIcon icon="exclamation-triangle" className="me-2" />
                {error}
              </Alert>
            )}

            <Form>
              {/* INPE Code */}
              <FormGroup className="form-group-custom">
                <Label for="inpeCode" className="form-label">
                  <FontAwesomeIcon icon="id-card" className="label-icon" />
                  INPE Code *
                </Label>
                <Input
                  type="text"
                  id="inpeCode"
                  className="form-input"
                  placeholder="Enter your INPE code"
                  value={inpeCode}
                  onChange={e => setInpeCode(e.target.value)}
                  invalid={!inpeCode.trim() && error !== null}
                />
              </FormGroup>

              {/* Phone Number */}
              <FormGroup className="form-group-custom">
                <Label for="phoneNumber" className="form-label">
                  <FontAwesomeIcon icon="phone" className="label-icon" />
                  Phone Number *
                </Label>
                <Input
                  type="tel"
                  id="phoneNumber"
                  className="form-input"
                  placeholder="Enter your phone number"
                  value={phoneNumber}
                  onChange={e => setPhoneNumber(e.target.value)}
                  invalid={!phoneNumber.trim() && error !== null}
                />
              </FormGroup>

              {/* Office Address */}
              <FormGroup className="form-group-custom">
                <Label for="officeAddress" className="form-label">
                  <FontAwesomeIcon icon="map-marker-alt" className="label-icon" />
                  Office Address *
                </Label>
                <Input
                  type="textarea"
                  id="officeAddress"
                  className="form-input"
                  placeholder="Enter your office address"
                  value={officeAddress}
                  onChange={e => setOfficeAddress(e.target.value)}
                  rows={3}
                  invalid={!officeAddress.trim() && error !== null}
                />
              </FormGroup>

              {/* Location Section */}
              <div className="location-section">
                <Label className="form-label">
                  <FontAwesomeIcon icon="location-arrow" className="label-icon" />
                  Location Coordinates
                </Label>

                <div className="location-status">
                  {locationLoading && (
                    <div className="location-loading">
                      <Spinner size="sm" className="me-2" />
                      Getting your location...
                    </div>
                  )}

                  {locationError && (
                    <div className="location-error">
                      <FontAwesomeIcon icon="exclamation-triangle" className="me-2" />
                      {locationError}
                    </div>
                  )}

                  {latitude !== null && longitude !== null && !locationLoading && (
                    <div className="location-success">
                      <FontAwesomeIcon icon="check-circle" className="me-2" />
                      Location detected: {latitude.toFixed(6)}, {longitude.toFixed(6)}
                    </div>
                  )}
                </div>

                <div className="location-actions">
                  <Button
                    type="button"
                    color="outline-primary"
                    size="sm"
                    onClick={getCurrentLocation}
                    disabled={locationLoading}
                    className="location-button"
                  >
                    <FontAwesomeIcon icon="crosshairs" className="me-2" />
                    {locationLoading ? 'Getting Location...' : 'Get My Location'}
                  </Button>
                </div>

                {/* Manual coordinate input (hidden by default, can be shown if needed) */}
                <div className="manual-coordinates" style={{ display: 'none' }}>
                  <div className="row">
                    <div className="col-md-6">
                      <FormGroup>
                        <Label for="latitude" className="form-label-small">
                          Latitude
                        </Label>
                        <Input
                          type="number"
                          id="latitude"
                          className="form-input-small"
                          step="any"
                          value={latitude || ''}
                          onChange={e => setLatitude(e.target.value ? parseFloat(e.target.value) : null)}
                        />
                      </FormGroup>
                    </div>
                    <div className="col-md-6">
                      <FormGroup>
                        <Label for="longitude" className="form-label-small">
                          Longitude
                        </Label>
                        <Input
                          type="number"
                          id="longitude"
                          className="form-input-small"
                          step="any"
                          value={longitude || ''}
                          onChange={e => setLongitude(e.target.value ? parseFloat(e.target.value) : null)}
                        />
                      </FormGroup>
                    </div>
                  </div>
                </div>
              </div>

              {/* Submit Button */}
              <div className="form-actions">
                <Button color="primary" size="lg" className="submit-button" onClick={submit} disabled={saving || locationLoading}>
                  {saving ? (
                    <>
                      <Spinner size="sm" className="me-2" />
                      Creating Profile...
                    </>
                  ) : (
                    <>
                      <FontAwesomeIcon icon="check" className="me-2" />
                      Complete Profile
                    </>
                  )}
                </Button>
              </div>
            </Form>
          </div>
        </Card>

        <div className="text-center mt-4">
          <small className="text-muted">Your profile will be reviewed by our admin team for verification</small>
        </div>
      </div>
    </div>
  );
};

export default CompleteDoctorProfile;
