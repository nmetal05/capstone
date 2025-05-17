import './home.scss';

import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Translate } from 'react-jhipster';
import { Alert, Col, Row, Button, Form, FormGroup, Label, Input } from 'reactstrap';
import axios from 'axios';

import { REDIRECT_URL, getLoginUrl } from 'app/shared/util/url-utils';
import { useAppSelector } from 'app/config/store';

interface Suggestion {
  specialization: string;
  confidence: number;
  reason: string;
}

interface ApiResponse {
  suggestions: Suggestion[];
}

interface Specialization {
  id: number;
  name: string;
  description: string;
}

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

export const Home = () => {
  const account = useAppSelector(state => state.authentication.account);
  const pageLocation = useLocation();
  const navigate = useNavigate();
  const [symptoms, setSymptoms] = useState('');
  const [suggestions, setSuggestions] = useState<Suggestion[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [specializations, setSpecializations] = useState<Specialization[]>([]);
  const [selectedSpec, setSelectedSpec] = useState('');
  const [radius, setRadius] = useState(10);
  const [openNowOnly, setOpenNowOnly] = useState(false);
  const [sortBy, setSortBy] = useState('distance');
  const [sortDir, setSortDir] = useState('asc');

  useEffect(() => {
    const redirectURL = localStorage.getItem(REDIRECT_URL);
    if (redirectURL) {
      localStorage.removeItem(REDIRECT_URL);
      location.href = `${location.origin}${redirectURL}`;
    }

    // Load specializations
    axios
      .get('/api/specializations?page=0&size=100')
      .then(response => {
        setSpecializations(response.data);
      })
      .catch(err => {
        console.error('Failed to load specializations', err);
      });
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!symptoms.trim()) {
      setError('Please describe your symptoms');
      return;
    }

    setIsLoading(true);
    setError('');

    try {
      const response = await axios.post<ApiResponse>('/api/ai/symptom-to-spec', {
        symptoms,
      });
      setSuggestions(response.data.suggestions);
    } catch (err) {
      setError('Failed to analyze symptoms. Please try again later.');
      console.error('API Error:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleFindDoctors = () => {
    if (!selectedSpec) {
      setError('Please select a specialization');
      return;
    }

    // Get user location (simplified - in real app you'd use geolocation API)
    const lat = 50.0; // Default latitude
    const lon = 30.0; // Default longitude

    navigate(
      `/doctors/nearby?spec=${encodeURIComponent(selectedSpec)}&lat=${lat}&lon=${lon}&radius=${radius}&sort=${sortBy}&dir=${sortDir}&openNow=${openNowOnly}`,
    );
  };

  const getConfidenceColor = (confidence: number) => {
    if (confidence >= 0.8) return 'success';
    if (confidence >= 0.6) return 'info';
    if (confidence >= 0.4) return 'warning';
    return 'danger';
  };

  return (
    <div className="home-container">
      <Row className="justify-content-center">
        <Col md="8" lg="6" className="home-content">
          <div className="hero-section">
            <h1 className="display-4 gradient-text">
              <Translate contentKey="home.title">Welcome, Java Hipster!</Translate>
            </h1>
          </div>

          {account?.login ? (
            <div className="auth-card logged-in">
              <Alert color="success" className="rounded-lg shadow-sm">
                <div className="d-flex align-items-center">
                  <i className="fas fa-check-circle me-3"></i>
                  <Translate contentKey="home.logged.message" interpolate={{ username: account.login }}>
                    You are logged in as user {account.login}.
                  </Translate>
                </div>
              </Alert>
            </div>
          ) : (
            <div className="auth-card">
              <Alert color="light" className="rounded-lg shadow-sm">
                <div className="d-flex align-items-start">
                  <i className="fas fa-info-circle me-3 mt-1"></i>
                  <div>
                    <Translate contentKey="global.messages.info.authenticated.prefix">If you want to </Translate>
                    <a
                      className="auth-link"
                      onClick={() =>
                        navigate(getLoginUrl(), {
                          state: { from: pageLocation },
                        })
                      }
                    >
                      <Translate contentKey="global.messages.info.authenticated.link">sign in</Translate>
                    </a>
                    <Translate contentKey="global.messages.info.authenticated.suffix">
                      , you can try the default accounts:
                      <br />- Administrator (login=&quot;admin&quot; and password=&quot;admin&quot;)
                      <br />- User (login=&quot;user&quot; and password=&quot;user&quot;).
                    </Translate>
                  </div>
                </div>
              </Alert>
            </div>
          )}

          {/* Symptom Analysis and Doctor Finder Sections */}
          <div className="services-grid mt-5">
            <Row>
              {/* Symptom Analysis Card */}
              <Col md="6" className="mb-4">
                <div className="card shadow-sm h-100">
                  <div className="card-body">
                    <h3 className="card-title mb-4">
                      <i className="fas fa-stethoscope me-2"></i>
                      Symptom Analysis
                    </h3>
                    <p className="text-muted mb-4">
                      Describe your symptoms and we&apos;ll suggest the most appropriate medical specialists for your condition.
                    </p>

                    <Form onSubmit={handleSubmit}>
                      <div className="mb-3">
                        <textarea
                          className="form-control"
                          rows={5}
                          placeholder="Describe your symptoms in detail (e.g., 'I've been experiencing dizziness and headaches for the past week')"
                          value={symptoms}
                          onChange={e => setSymptoms(e.target.value)}
                          disabled={isLoading}
                        />
                      </div>

                      {error && <Alert color="danger">{error}</Alert>}

                      <Button color="primary" type="submit" disabled={isLoading} className="w-100 py-2">
                        {isLoading ? (
                          <>
                            <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                            Analyzing...
                          </>
                        ) : (
                          <>
                            <i className="fas fa-search me-2"></i>
                            Analyze Symptoms
                          </>
                        )}
                      </Button>
                    </Form>

                    {suggestions.length > 0 && (
                      <div className="mt-4">
                        <h5 className="mb-3">Recommended Specialists:</h5>
                        <div className="suggestions-grid">
                          {suggestions.map((suggestion, index) => (
                            <div key={index} className="suggestion-card">
                              <Alert color={getConfidenceColor(suggestion.confidence)} className="rounded-lg">
                                <div className="d-flex justify-content-between align-items-start">
                                  <h5 className="mb-2">{suggestion.specialization}</h5>
                                  <span className="badge bg-white text-dark">{Math.round(suggestion.confidence * 100)}% confidence</span>
                                </div>
                                <p className="mb-0">{suggestion.reason}</p>
                              </Alert>
                            </div>
                          ))}
                        </div>
                      </div>
                    )}
                  </div>
                </div>
              </Col>

              {/* Find Doctors Card */}
              <Col md="6" className="mb-4">
                <div className="card shadow-sm h-100">
                  <div className="card-body">
                    <h3 className="card-title mb-4">
                      <i className="fas fa-map-marker-alt me-2"></i>
                      Find Nearby Doctors
                    </h3>
                    <p className="text-muted mb-4">Search for medical specialists in your area with specific criteria.</p>

                    <Form>
                      <FormGroup>
                        <Label for="specialization">Specialization</Label>
                        <Input type="select" id="specialization" value={selectedSpec} onChange={e => setSelectedSpec(e.target.value)}>
                          <option value="">Select a specialization</option>
                          {specializations.map(spec => (
                            <option key={spec.id} value={spec.name}>
                              {spec.name}
                            </option>
                          ))}
                        </Input>
                      </FormGroup>

                      <FormGroup>
                        <Label for="radius">Search Radius: {radius} km</Label>
                        <div className="d-flex align-items-center">
                          <input
                            type="range"
                            className="form-range flex-grow-1 me-3"
                            id="radius"
                            min={1}
                            max={50}
                            step={1}
                            value={radius}
                            onChange={e => setRadius(parseInt(e.target.value, 10))}
                          />
                          <span className="badge bg-primary">{radius} km</span>
                        </div>
                      </FormGroup>

                      <FormGroup check>
                        <Input type="checkbox" id="openNow" checked={openNowOnly} onChange={e => setOpenNowOnly(e.target.checked)} />
                        <Label for="openNow" check>
                          Only show currently open
                        </Label>
                      </FormGroup>

                      <FormGroup>
                        <Label for="sortBy">Sort By</Label>
                        <div className="d-flex">
                          <Input type="select" id="sortBy" value={sortBy} onChange={e => setSortBy(e.target.value)} className="me-2">
                            <option value="distance">Distance</option>
                            <option value="rating">Rating</option>
                          </Input>
                          <Input type="select" value={sortDir} onChange={e => setSortDir(e.target.value)}>
                            <option value="asc">Ascending</option>
                            <option value="desc">Descending</option>
                          </Input>
                        </div>
                      </FormGroup>

                      <Button color="primary" onClick={handleFindDoctors} className="w-100 py-2 mt-3" disabled={!selectedSpec}>
                        <i className="fas fa-search-location me-2"></i>
                        Find Doctors
                      </Button>
                    </Form>
                  </div>
                </div>
              </Col>
            </Row>
          </div>
        </Col>
      </Row>
    </div>
  );
};

export default Home;
