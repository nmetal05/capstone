import './user-history.scss';
import React, { useEffect, useState } from 'react';
import {
  Card,
  CardBody,
  CardHeader,
  Row,
  Col,
  Nav,
  NavItem,
  NavLink,
  TabContent,
  TabPane,
  Badge,
  Button,
  Spinner,
  Alert,
  Collapse,
  Modal,
  ModalHeader,
  ModalBody,
  ModalFooter,
} from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { TextFormat } from 'react-jhipster';
import { APP_DATE_FORMAT } from 'app/config/constants';
import axios from 'axios';
import classnames from 'classnames';

// Interfaces
interface SymptomSearch {
  id: number;
  searchDate: string;
  symptoms: string;
  aiResponseJson: string;
  user?: any;
  guestSession?: any;
}

interface DoctorViewHistory {
  id: number;
  viewDate: string;
  user?: any;
  doctor?: any;
  googleDoctorData?: string; // JSON string for Google Maps doctors
}

interface GoogleDoctorData {
  placeId: string;
  doctorName: string;
  address: string;
  latitude: number;
  longitude: number;
  rating: number;
  userRatingCount: number;
  openNow: boolean;
  phoneNumber?: string;
  website?: string;
  weekdayDescriptions?: string;
}

interface SymptomSearchRecommendation {
  id: number;
  confidenceScore: number;
  rank: number;
  reasoning: string;
  search?: SymptomSearch;
  specialization?: {
    id: number;
    name: string;
    description: string;
  };
}

interface AIResponse {
  analysis?: string;
  recommendations?: Array<{
    specialization: string;
    confidence: number;
    reasoning: string;
  }>;
  urgency?: string;
  disclaimer?: string;
  possible_conditions?: Array<{
    name: string;
    probability?: number;
  }>;
  severity?: string;
  summary?: string;
  next_steps?: string[];
  red_flags?: string[];
  when_to_seek_help?: string;
}

const UserHistory: React.FC = () => {
  const [activeTab, setActiveTab] = useState('symptom-searches');
  const [symptomSearches, setSymptomSearches] = useState<SymptomSearch[]>([]);
  const [doctorViews, setDoctorViews] = useState<DoctorViewHistory[]>([]);
  const [recommendations, setRecommendations] = useState<{ [key: number]: SymptomSearchRecommendation[] }>({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [expandedSearches, setExpandedSearches] = useState<Set<number>>(new Set());
  const [showDoctorModal, setShowDoctorModal] = useState(false);
  const [selectedDoctorView, setSelectedDoctorView] = useState<DoctorViewHistory | null>(null);

  useEffect(() => {
    loadHistoryData();
  }, []);

  const loadHistoryData = async () => {
    try {
      setLoading(true);
      setError(null);

      const [symptomSearchResponse, doctorViewResponse] = await Promise.all([
        axios.get('/api/user-history/symptom-searches?size=50&sort=searchDate,desc'),
        axios.get('/api/user-history/doctor-views?size=50&sort=viewDate,desc'),
      ]);

      setSymptomSearches(symptomSearchResponse.data);
      setDoctorViews(doctorViewResponse.data);

      // Load recommendations for each symptom search
      const recommendationsMap: { [key: number]: SymptomSearchRecommendation[] } = {};
      for (const search of symptomSearchResponse.data) {
        try {
          const recResponse = await axios.get(`/api/user-history/symptom-search-recommendations/${search.id}`);
          recommendationsMap[search.id] = recResponse.data;
        } catch (err) {
          console.warn(`Failed to load recommendations for search ${search.id}:`, err);
          recommendationsMap[search.id] = [];
        }
      }
      setRecommendations(recommendationsMap);
    } catch (err) {
      console.error('Error loading history data:', err);
      setError('Failed to load history data. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const toggleTab = (tab: string) => {
    if (activeTab !== tab) {
      setActiveTab(tab);
    }
  };

  const toggleSearchExpansion = async (searchId: number) => {
    const newExpanded = new Set(expandedSearches);
    if (newExpanded.has(searchId)) {
      newExpanded.delete(searchId);
    } else {
      newExpanded.add(searchId);
      // Load recommendations when expanding for the first time
      if (!recommendations[searchId]) {
        await loadRecommendations(searchId);
      }
    }
    setExpandedSearches(newExpanded);
  };

  const loadRecommendations = async (searchId: number) => {
    try {
      const response = await axios.get(`/api/user-history/symptom-search-recommendations/${searchId}`);
      setRecommendations(prev => ({
        ...prev,
        [searchId]: response.data,
      }));
    } catch (err) {
      console.error('Failed to load recommendations for search:', searchId, err);
    }
  };

  const parseAIResponse = (aiResponseJson: string): AIResponse | null => {
    try {
      const parsed = JSON.parse(aiResponseJson);

      // Handle the actual AI response structure from the symptom analysis
      if (parsed.suggestions && Array.isArray(parsed.suggestions)) {
        return {
          analysis: `Based on your symptoms, our AI has identified ${parsed.suggestions.length} potential specialist recommendations.`,
          recommendations: parsed.suggestions.map((suggestion: any) => ({
            specialization: suggestion.specialization,
            confidence: suggestion.confidence,
            reasoning: suggestion.reason,
          })),
          summary: `AI analysis completed with ${parsed.suggestions.length} specialist recommendations.`,
          // Extract other fields if they exist
          urgency: parsed.urgency,
          severity: parsed.severity,
          disclaimer: parsed.disclaimer || 'This is an AI-generated analysis and should not replace professional medical advice.',
          possible_conditions: parsed.possible_conditions,
          next_steps: parsed.next_steps,
          red_flags: parsed.red_flags,
          when_to_seek_help: parsed.when_to_seek_help,
        };
      }

      // If it's already in the expected format, return as is
      return parsed;
    } catch (err) {
      console.warn('Failed to parse AI response:', err);
      return null;
    }
  };

  const formatSymptoms = (symptoms: string): string[] => {
    return symptoms
      .split(',')
      .map(s => s.trim())
      .filter(s => s.length > 0);
  };

  const getUrgencyColor = (urgency?: string): string => {
    switch (urgency?.toLowerCase()) {
      case 'high':
      case 'urgent':
        return 'danger';
      case 'medium':
      case 'moderate':
        return 'warning';
      case 'low':
        return 'success';
      default:
        return 'info';
    }
  };

  const getSeverityColor = (severity?: string): string => {
    switch (severity?.toLowerCase()) {
      case 'high':
      case 'urgent':
        return 'danger';
      case 'medium':
      case 'moderate':
        return 'warning';
      case 'low':
        return 'success';
      default:
        return 'info';
    }
  };

  const handleShowDoctorDetails = (doctorView: DoctorViewHistory) => {
    setSelectedDoctorView(doctorView);
    setShowDoctorModal(true);
  };

  const handleCloseDoctorModal = () => {
    setShowDoctorModal(false);
    setSelectedDoctorView(null);
  };

  const parseGoogleDoctorData = (googleDoctorDataJson: string): GoogleDoctorData | null => {
    try {
      return JSON.parse(googleDoctorDataJson);
    } catch (err) {
      console.warn('Failed to parse Google doctor data:', err);
      return null;
    }
  };

  const isGoogleMapsDoctor = (view: DoctorViewHistory): boolean => {
    return view.googleDoctorData != null && view.doctor == null;
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

  const getDoctorDisplayInfo = (view: DoctorViewHistory) => {
    if (isGoogleMapsDoctor(view)) {
      const googleData = parseGoogleDoctorData(view.googleDoctorData);
      return {
        name: googleData?.doctorName || 'Google Maps Doctor',
        isGoogle: true,
        googleData,
        doctor: null,
      };
    } else {
      return {
        name: formatDoctorName(view.doctor?.internalUser),
        isGoogle: false,
        googleData: null,
        doctor: view.doctor,
      };
    }
  };

  const renderDoctorSpecializations = (view: DoctorViewHistory, isGoogle: boolean) => {
    if (isGoogle || !view.doctor?.specializations || view.doctor.specializations.length === 0) {
      return null;
    }

    return (
      <div className="mb-2">
        <strong>Specializations:</strong>
        <div className="mt-1">
          {view.doctor.specializations.map((spec: any, index: number) => (
            <Badge key={index} color="primary" className="me-1 mb-1">
              {spec.name}
            </Badge>
          ))}
        </div>
      </div>
    );
  };

  const renderDoctorContactInfo = (view: DoctorViewHistory, isGoogle: boolean, googleData: GoogleDoctorData | null) => {
    return (
      <>
        {/* Phone Number */}
        {((isGoogle && googleData?.phoneNumber) || (!isGoogle && view.doctor?.phoneNumber && view.doctor.phoneNumber !== 'N/A')) && (
          <div className="mb-2">
            <strong>Phone:</strong>
            <div className="text-muted">
              <FontAwesomeIcon icon="phone" className="me-2" />
              {isGoogle ? googleData?.phoneNumber : view.doctor?.phoneNumber}
            </div>
          </div>
        )}

        {/* Email - only for regular doctors */}
        {!isGoogle && view.doctor?.internalUser?.email && !view.doctor.internalUser.email.includes('@googlemaps.placeholder') && (
          <div className="mb-2">
            <strong>Email:</strong>
            <div className="text-muted">
              <FontAwesomeIcon icon="envelope" className="me-2" />
              {view.doctor.internalUser.email}
            </div>
          </div>
        )}

        {/* Website - only for Google Maps doctors */}
        {isGoogle && googleData?.website && (
          <div className="mb-2">
            <strong>Website:</strong>
            <div className="text-muted">
              <FontAwesomeIcon icon="globe" className="me-2" />
              <a href={googleData.website} target="_blank" rel="noopener noreferrer">
                {googleData.website}
              </a>
            </div>
          </div>
        )}

        {/* Office Address */}
        {(isGoogle || view.doctor?.officeAddress) && (
          <div className="mb-2">
            <strong>Address:</strong>
            <div className="text-muted">
              <FontAwesomeIcon icon="map-marker-alt" className="me-2" />
              {isGoogle
                ? googleData?.address
                : view.doctor?.officeAddress?.includes('[Google Maps:')
                  ? view.doctor.officeAddress.split(' [Google Maps:')[0]
                  : view.doctor?.officeAddress}
            </div>
          </div>
        )}

        {/* Rating - only for Google Maps doctors */}
        {isGoogle && googleData?.rating && googleData.rating > 0 && (
          <div className="mb-2">
            <strong>Rating:</strong>
            <div className="text-muted">
              <FontAwesomeIcon icon="star" className="me-2 text-warning" />
              {googleData.rating}/5 ({googleData.userRatingCount} reviews)
            </div>
          </div>
        )}
      </>
    );
  };

  const renderDoctorVerificationStatus = (view: DoctorViewHistory, isGoogle: boolean) => {
    if (isGoogle) {
      return (
        <Badge color="info" className="small">
          <FontAwesomeIcon icon={['fab', 'google']} className="me-1" />
          Google Maps Provider
        </Badge>
      );
    }

    if (view.doctor?.isVerified !== undefined) {
      return (
        <Badge color={view.doctor.isVerified ? 'success' : 'warning'} className="small">
          <FontAwesomeIcon icon={view.doctor.isVerified ? 'check-circle' : 'clock'} className="me-1" />
          {view.doctor.isVerified ? 'Verified' : 'Pending Verification'}
        </Badge>
      );
    }

    return null;
  };

  const renderDoctorCard = (view: DoctorViewHistory) => {
    const { name, isGoogle, googleData } = getDoctorDisplayInfo(view);

    return (
      <Card key={view.id} className="mb-3 shadow-sm">
        <CardBody>
          <div className="d-flex justify-content-between align-items-start mb-2">
            <div>
              <h5 className="mb-1">{name}</h5>
              <small className="text-muted">
                <FontAwesomeIcon icon="calendar-alt" className="me-1" />
                <TextFormat value={view.viewDate} type="date" format={APP_DATE_FORMAT} />
              </small>
            </div>
          </div>

          {/* Doctor Information */}
          <div className="mt-3">
            {/* Check if this is a Google Maps doctor */}
            {isGoogle && (
              <div className="mb-2">
                <Badge color="info" className="me-2">
                  <FontAwesomeIcon icon={['fab', 'google']} className="me-1" />
                  Google Maps
                </Badge>
              </div>
            )}

            {/* Specializations */}
            {renderDoctorSpecializations(view, isGoogle)}

            {/* Contact Information */}
            {renderDoctorContactInfo(view, isGoogle, googleData)}
          </div>

          {/* Verification Status */}
          <div className="d-flex align-items-center justify-content-between">
            <div>{renderDoctorVerificationStatus(view, isGoogle)}</div>
            <Button color="outline-primary" size="sm" onClick={() => handleShowDoctorDetails(view)}>
              <FontAwesomeIcon icon="eye" className="me-1" />
              View Details
            </Button>
          </div>
        </CardBody>
      </Card>
    );
  };

  const renderDoctorViews = () => (
    <div className="doctor-views-tab">
      {doctorViews.length === 0 ? (
        <Card className="text-center">
          <CardBody className="py-5">
            <FontAwesomeIcon icon="user-md" size="3x" className="text-muted mb-3" />
            <h4 className="text-muted">No Doctor Views Yet</h4>
            <p className="text-muted">Your doctor profile viewing history will appear here.</p>
          </CardBody>
        </Card>
      ) : (
        doctorViews.map(renderDoctorCard)
      )}
    </div>
  );

  const renderSymptomSearches = () => (
    <div className="symptom-searches-tab">
      {symptomSearches.length === 0 ? (
        <Card className="text-center">
          <CardBody className="py-5">
            <FontAwesomeIcon icon="search" size="3x" className="text-muted mb-3" />
            <h4 className="text-muted">No Symptom Searches Yet</h4>
            <p className="text-muted">Your AI symptom analysis history will appear here.</p>
          </CardBody>
        </Card>
      ) : (
        symptomSearches.map(search => {
          const aiResponse = parseAIResponse(search.aiResponseJson);
          const isExpanded = expandedSearches.has(search.id);
          const searchRecommendations = recommendations[search.id] || [];

          return (
            <Card key={search.id} className="mb-4 shadow-sm">
              <CardHeader className="bg-light">
                <div className="d-flex justify-content-between align-items-center">
                  <div className="d-flex align-items-center">
                    <FontAwesomeIcon icon="stethoscope" className="text-primary me-2" />
                    <div>
                      <h6 className="mb-0">
                        <TextFormat value={search.searchDate} type="date" format={APP_DATE_FORMAT} />
                      </h6>
                      <small className="text-muted">Symptom Analysis #{search.id}</small>
                    </div>
                  </div>
                  <Button color="link" size="sm" onClick={() => toggleSearchExpansion(search.id)} className="p-0">
                    <FontAwesomeIcon icon={isExpanded ? 'chevron-up' : 'chevron-down'} />
                  </Button>
                </div>
              </CardHeader>
              <CardBody>
                {/* Symptoms */}
                <div className="mb-3">
                  <h6 className="text-dark mb-2">
                    <FontAwesomeIcon icon="list-ul" className="me-2" />
                    Reported Symptoms
                  </h6>
                  <div className="d-flex flex-wrap gap-2">
                    {formatSymptoms(search.symptoms).map((symptom, index) => (
                      <Badge key={index} color="secondary" pill>
                        {symptom}
                      </Badge>
                    ))}
                  </div>
                </div>

                {/* AI Analysis Summary */}
                {aiResponse && (
                  <div className="mb-3">
                    <div className="d-flex align-items-center justify-content-between mb-2">
                      <h6 className="text-dark mb-0">
                        <FontAwesomeIcon icon="robot" className="me-2" />
                        AI Analysis
                      </h6>
                      {aiResponse.urgency && <Badge color={getUrgencyColor(aiResponse.urgency)}>{aiResponse.urgency} Priority</Badge>}
                    </div>

                    {/* Summary */}
                    {aiResponse.summary && (
                      <div className="bg-primary bg-opacity-10 p-3 rounded mb-2">
                        <h6 className="text-primary mb-2">
                          <FontAwesomeIcon icon="clipboard-list" className="me-1" />
                          Summary
                        </h6>
                        <p className="mb-0">{aiResponse.summary}</p>
                      </div>
                    )}

                    {/* Main Analysis */}
                    {aiResponse.analysis && (
                      <div className="bg-light p-3 rounded mb-2">
                        <h6 className="text-dark mb-2">
                          <FontAwesomeIcon icon="search" className="me-1" />
                          Detailed Analysis
                        </h6>
                        <p className="mb-0">{aiResponse.analysis}</p>
                      </div>
                    )}

                    {/* Possible Conditions */}
                    {aiResponse.possible_conditions && aiResponse.possible_conditions.length > 0 && (
                      <div className="mb-2">
                        <small className="text-muted fw-bold">Possible Conditions:</small>
                        <div className="mt-1">
                          {aiResponse.possible_conditions.map((condition: any, index: number) => (
                            <Badge key={index} color="info" className="me-1 mb-1">
                              {typeof condition === 'string' ? condition : condition.name || condition.condition}
                              {condition.probability && ` (${Math.round(condition.probability * 100)}%)`}
                            </Badge>
                          ))}
                        </div>
                      </div>
                    )}

                    {/* Red Flags */}
                    {aiResponse.red_flags && aiResponse.red_flags.length > 0 && (
                      <div className="mb-2">
                        <div className="bg-danger bg-opacity-10 p-3 rounded">
                          <h6 className="text-danger mb-2">
                            <FontAwesomeIcon icon="exclamation-triangle" className="me-1" />
                            Warning Signs
                          </h6>
                          <ul className="list-unstyled mb-0">
                            {aiResponse.red_flags.map((flag: string, index: number) => (
                              <li key={index} className="text-danger mb-1">
                                <FontAwesomeIcon icon="exclamation-circle" className="me-2" />
                                {flag}
                              </li>
                            ))}
                          </ul>
                        </div>
                      </div>
                    )}

                    {/* When to Seek Help */}
                    {aiResponse.when_to_seek_help && (
                      <div className="mb-2">
                        <div className="bg-warning bg-opacity-10 p-3 rounded">
                          <h6 className="text-warning mb-2">
                            <FontAwesomeIcon icon="hospital" className="me-1" />
                            When to Seek Medical Help
                          </h6>
                          <p className="mb-0 text-dark">{aiResponse.when_to_seek_help}</p>
                        </div>
                      </div>
                    )}

                    {/* Next Steps */}
                    {aiResponse.next_steps && aiResponse.next_steps.length > 0 && (
                      <div className="mb-2">
                        <div className="bg-success bg-opacity-10 p-3 rounded">
                          <h6 className="text-success mb-2">
                            <FontAwesomeIcon icon="list-check" className="me-1" />
                            Recommended Next Steps
                          </h6>
                          <ol className="mb-0">
                            {aiResponse.next_steps.map((step: string, index: number) => (
                              <li key={index} className="text-dark mb-1">
                                {step}
                              </li>
                            ))}
                          </ol>
                        </div>
                      </div>
                    )}

                    {/* Recommendations from AI */}
                    {aiResponse.recommendations && aiResponse.recommendations.length > 0 && (
                      <div className="mb-2">
                        <small className="text-muted fw-bold">AI Recommendations:</small>
                        <ul className="list-unstyled mt-1 mb-0">
                          {aiResponse.recommendations.map((rec: any, index: number) => (
                            <li key={index} className="small text-muted">
                              <FontAwesomeIcon icon="chevron-right" className="me-1" />
                              {typeof rec === 'string' ? rec : rec.recommendation || rec.text}
                            </li>
                          ))}
                        </ul>
                      </div>
                    )}

                    {/* Severity Assessment */}
                    {aiResponse.severity && (
                      <div className="mb-2">
                        <small className="text-muted fw-bold">Severity: </small>
                        <Badge color={getSeverityColor(aiResponse.severity)} className="small">
                          {aiResponse.severity}
                        </Badge>
                      </div>
                    )}
                  </div>
                )}

                {/* AI Response Details */}
                {aiResponse && (
                  <div className="mb-4">
                    <h6 className="text-dark mb-3">
                      <FontAwesomeIcon icon="robot" className="me-2" />
                      AI Analysis Details
                    </h6>

                    {/* Summary */}
                    {aiResponse.summary && (
                      <Alert color="info" className="mb-3">
                        <FontAwesomeIcon icon="info-circle" className="me-2" />
                        <strong>Summary:</strong> {aiResponse.summary}
                      </Alert>
                    )}

                    {/* Analysis */}
                    {aiResponse.analysis && (
                      <Alert color="light" className="mb-3">
                        <FontAwesomeIcon icon="stethoscope" className="me-2" />
                        <strong>Analysis:</strong> {aiResponse.analysis}
                      </Alert>
                    )}

                    {/* AI Recommendations */}
                    {aiResponse.recommendations && aiResponse.recommendations.length > 0 && (
                      <div className="mb-3">
                        <h6 className="text-primary mb-2">AI Specialist Recommendations:</h6>
                        {aiResponse.recommendations.map((rec, index) => (
                          <Card key={index} className="mb-2 border-left-primary">
                            <CardBody className="py-2">
                              <div className="d-flex justify-content-between align-items-start mb-1">
                                <h6 className="mb-0 text-primary">{rec.specialization}</h6>
                                <Badge color="success">{Math.round(rec.confidence * 100)}% confidence</Badge>
                              </div>
                              <p className="text-muted mb-0 small">{rec.reasoning}</p>
                            </CardBody>
                          </Card>
                        ))}
                      </div>
                    )}

                    {/* Red Flags */}
                    {aiResponse.red_flags && aiResponse.red_flags.length > 0 && (
                      <Alert color="danger" className="mb-3">
                        <FontAwesomeIcon icon="exclamation-triangle" className="me-2" />
                        <strong>Warning Signs:</strong>
                        <ul className="mb-0 mt-2">
                          {aiResponse.red_flags.map((flag, index) => (
                            <li key={index}>{flag}</li>
                          ))}
                        </ul>
                      </Alert>
                    )}

                    {/* When to Seek Help */}
                    {aiResponse.when_to_seek_help && (
                      <Alert color="warning" className="mb-3">
                        <FontAwesomeIcon icon="clock" className="me-2" />
                        <strong>When to Seek Help:</strong> {aiResponse.when_to_seek_help}
                      </Alert>
                    )}

                    {/* Next Steps */}
                    {aiResponse.next_steps && aiResponse.next_steps.length > 0 && (
                      <Alert color="success" className="mb-3">
                        <FontAwesomeIcon icon="list-check" className="me-2" />
                        <strong>Next Steps:</strong>
                        <ul className="mb-0 mt-2">
                          {aiResponse.next_steps.map((step, index) => (
                            <li key={index}>{step}</li>
                          ))}
                        </ul>
                      </Alert>
                    )}

                    {/* Possible Conditions */}
                    {aiResponse.possible_conditions && aiResponse.possible_conditions.length > 0 && (
                      <div className="mb-3">
                        <h6 className="text-dark mb-2">Possible Conditions:</h6>
                        {aiResponse.possible_conditions.map((condition, index) => (
                          <Badge key={index} color="outline-secondary" className="me-2 mb-1">
                            {condition.name}
                            {condition.probability && ` (${Math.round(condition.probability * 100)}%)`}
                          </Badge>
                        ))}
                      </div>
                    )}

                    {/* Urgency and Severity */}
                    <div className="d-flex gap-2 mb-3">
                      {aiResponse.urgency && (
                        <Badge color={getUrgencyColor(aiResponse.urgency)} className="px-3 py-2">
                          <FontAwesomeIcon icon="tachometer-alt" className="me-1" />
                          Urgency: {aiResponse.urgency}
                        </Badge>
                      )}
                      {aiResponse.severity && (
                        <Badge color={getSeverityColor(aiResponse.severity)} className="px-3 py-2">
                          <FontAwesomeIcon icon="thermometer-half" className="me-1" />
                          Severity: {aiResponse.severity}
                        </Badge>
                      )}
                    </div>

                    {/* Disclaimer */}
                    {aiResponse.disclaimer && (
                      <Alert color="warning" className="small mb-0">
                        <FontAwesomeIcon icon="exclamation-triangle" className="me-2" />
                        {aiResponse.disclaimer}
                      </Alert>
                    )}
                  </div>
                )}

                {/* Recommendations Preview */}
                {searchRecommendations.length > 0 && (
                  <div className="mb-3">
                    <h6 className="text-dark mb-2">
                      <FontAwesomeIcon icon="user-md" className="me-2" />
                      Recommended Specializations
                    </h6>
                    <div className="d-flex flex-wrap gap-2">
                      {searchRecommendations.slice(0, 3).map(rec => (
                        <Badge key={rec.id} color="primary" pill>
                          {rec.specialization?.name} ({Math.round(rec.confidenceScore * 100)}%)
                        </Badge>
                      ))}
                      {searchRecommendations.length > 3 && (
                        <Badge color="light" pill>
                          +{searchRecommendations.length - 3} more
                        </Badge>
                      )}
                    </div>
                  </div>
                )}

                {/* Expanded Details */}
                <Collapse isOpen={isExpanded}>
                  <hr />

                  {/* Detailed Recommendations */}
                  {searchRecommendations.length > 0 && (
                    <div className="mb-4">
                      <h6 className="text-dark mb-3">
                        <FontAwesomeIcon icon="user-md" className="me-2" />
                        Detailed Specialist Recommendations
                      </h6>
                      {searchRecommendations.map(rec => (
                        <Card key={rec.id} className="mb-2 border-left-primary">
                          <CardBody className="py-3">
                            <div className="d-flex justify-content-between align-items-start mb-2">
                              <div>
                                <h6 className="mb-1 text-primary">
                                  <FontAwesomeIcon icon="stethoscope" className="me-2" />
                                  {rec.specialization?.name}
                                </h6>
                                <small className="text-muted">Rank #{rec.rank}</small>
                              </div>
                              <Badge color="success" className="px-3 py-2">
                                <FontAwesomeIcon icon="percentage" className="me-1" />
                                {Math.round(rec.confidenceScore * 100)}% confidence
                              </Badge>
                            </div>
                            <p className="text-muted mb-2">
                              <FontAwesomeIcon icon="lightbulb" className="me-2" />
                              <strong>Reasoning:</strong> {rec.reasoning}
                            </p>
                            {rec.specialization?.description && (
                              <p className="text-muted mb-0 small">
                                <FontAwesomeIcon icon="info-circle" className="me-2" />
                                <strong>About this specialty:</strong> {rec.specialization.description}
                              </p>
                            )}
                          </CardBody>
                        </Card>
                      ))}
                    </div>
                  )}
                </Collapse>
              </CardBody>
            </Card>
          );
        })
      )}
    </div>
  );

  const renderContactInfo = (isGoogle: boolean, googleData: GoogleDoctorData | null, doctor: any) => (
    <Card className="mb-4 border-0" style={{ backgroundColor: '#f8f9fa' }}>
      <CardBody>
        <h6 className="mb-3">
          <FontAwesomeIcon icon="address-book" className="text-primary me-2" />
          Contact Information
        </h6>

        {/* Phone */}
        {((isGoogle && googleData?.phoneNumber) || (!isGoogle && doctor?.phoneNumber && doctor.phoneNumber !== 'N/A')) && (
          <div className="mb-2">
            <strong>Phone:</strong>
            <div className="text-muted">
              <FontAwesomeIcon icon="phone" className="me-2" />
              {isGoogle ? googleData?.phoneNumber : doctor?.phoneNumber}
            </div>
          </div>
        )}

        {/* Email - only for regular doctors */}
        {!isGoogle && doctor?.internalUser?.email && !doctor.internalUser.email.includes('@googlemaps.placeholder') && (
          <div className="mb-2">
            <strong>Email:</strong>
            <div className="text-muted">
              <FontAwesomeIcon icon="envelope" className="me-2" />
              {doctor.internalUser.email}
            </div>
          </div>
        )}

        {/* Website - only for Google Maps doctors */}
        {isGoogle && googleData?.website && (
          <div className="mb-2">
            <strong>Website:</strong>
            <div className="text-muted">
              <FontAwesomeIcon icon="globe" className="me-2" />
              <a href={googleData.website} target="_blank" rel="noopener noreferrer">
                {googleData.website}
              </a>
            </div>
          </div>
        )}

        {/* Address */}
        {(isGoogle || doctor?.officeAddress) && (
          <div>
            <strong>Office Address:</strong>
            <div className="text-muted">
              <FontAwesomeIcon icon="map-marker-alt" className="me-2" />
              {isGoogle
                ? googleData?.address
                : doctor?.officeAddress?.includes('[Google Maps:')
                  ? doctor.officeAddress.split(' [Google Maps:')[0]
                  : doctor?.officeAddress}
            </div>
          </div>
        )}
      </CardBody>
    </Card>
  );

  const renderDoctorModal = () => {
    if (!showDoctorModal || !selectedDoctorView) return null;

    const { name, isGoogle, googleData } = getDoctorDisplayInfo(selectedDoctorView);

    return (
      <Modal isOpen={showDoctorModal} toggle={handleCloseDoctorModal} size="lg">
        <ModalHeader toggle={handleCloseDoctorModal}>
          <div className="d-flex align-items-center">
            <FontAwesomeIcon icon="user-md" className="text-primary me-2" />
            Doctor Details
          </div>
        </ModalHeader>
        <ModalBody>
          <div className="doctor-details">
            {/* Doctor Header */}
            <div className="text-center mb-4">
              <div
                className="mx-auto mb-3 d-flex align-items-center justify-content-center"
                style={{
                  width: '80px',
                  height: '80px',
                  borderRadius: '50%',
                  backgroundColor: isGoogle ? '#f3e5f5' : '#e3f2fd',
                  color: isGoogle ? '#7b1fa2' : '#1976d2',
                }}
              >
                <FontAwesomeIcon icon={isGoogle ? ['fab', 'google'] : 'user-md'} style={{ fontSize: '2rem' }} />
              </div>
              <h4 className="mb-2">{name}</h4>
              {isGoogle ? (
                <Badge color="info" className="px-3 py-2" style={{ borderRadius: '15px' }}>
                  <FontAwesomeIcon icon={['fab', 'google']} className="me-1" />
                  Google Maps Provider
                </Badge>
              ) : (
                <Badge color="primary" className="px-3 py-2" style={{ borderRadius: '15px' }}>
                  <FontAwesomeIcon icon="hospital" className="me-1" />
                  Our Network Doctor
                </Badge>
              )}
            </div>

            {/* View Information */}
            <Card className="mb-4 border-0" style={{ backgroundColor: '#f8f9fa' }}>
              <CardBody>
                <h6 className="mb-3">
                  <FontAwesomeIcon icon="clock" className="text-primary me-2" />
                  View Information
                </h6>
                <div className="mb-2">
                  <strong>Viewed on:</strong>
                  <div className="text-muted">
                    <TextFormat value={selectedDoctorView.viewDate} type="date" format={APP_DATE_FORMAT} />
                  </div>
                </div>
                <div>
                  <strong>View ID:</strong>
                  <div className="text-muted">#{selectedDoctorView.id}</div>
                </div>
              </CardBody>
            </Card>

            {/* Specializations - only for regular doctors */}
            {!isGoogle && selectedDoctorView.doctor?.specializations && selectedDoctorView.doctor.specializations.length > 0 && (
              <Card className="mb-4 border-0" style={{ backgroundColor: '#f8f9fa' }}>
                <CardBody>
                  <h6 className="mb-3">
                    <FontAwesomeIcon icon="stethoscope" className="text-primary me-2" />
                    Specializations
                  </h6>
                  <div className="d-flex flex-wrap gap-2">
                    {selectedDoctorView.doctor.specializations.map((spec: any, index: number) => (
                      <Badge key={index} color="primary" pill className="px-3 py-2">
                        {spec.name}
                      </Badge>
                    ))}
                  </div>
                </CardBody>
              </Card>
            )}

            {/* Contact Information */}
            {renderContactInfo(isGoogle, googleData, selectedDoctorView.doctor)}

            {/* Rating - only for Google Maps doctors */}
            {isGoogle && googleData?.rating && googleData.rating > 0 && (
              <Card className="mb-4 border-0" style={{ backgroundColor: '#f8f9fa' }}>
                <CardBody>
                  <h6 className="mb-3">
                    <FontAwesomeIcon icon="star" className="text-primary me-2" />
                    Rating & Reviews
                  </h6>
                  <div className="d-flex align-items-center">
                    <div className="me-3">
                      <span className="h4 text-warning">{googleData.rating}</span>
                      <span className="text-muted">/5</span>
                    </div>
                    <div>
                      <div className="text-muted">
                        <FontAwesomeIcon icon="users" className="me-1" />
                        {googleData.userRatingCount} reviews
                      </div>
                    </div>
                  </div>
                </CardBody>
              </Card>
            )}

            {/* Verification Status */}
            <Card className="mb-4 border-0" style={{ backgroundColor: '#f8f9fa' }}>
              <CardBody>
                <h6 className="mb-3">
                  <FontAwesomeIcon icon="shield-alt" className="text-primary me-2" />
                  Verification Status
                </h6>
                <div className="d-flex align-items-center">
                  {isGoogle ? (
                    <Badge color="info" className="px-3 py-2">
                      <FontAwesomeIcon icon={['fab', 'google']} className="me-1" />
                      Google Maps Provider
                    </Badge>
                  ) : selectedDoctorView.doctor?.isVerified !== undefined ? (
                    <Badge color={selectedDoctorView.doctor.isVerified ? 'success' : 'warning'} className="px-3 py-2">
                      <FontAwesomeIcon icon={selectedDoctorView.doctor.isVerified ? 'check-circle' : 'clock'} className="me-1" />
                      {selectedDoctorView.doctor.isVerified ? 'Verified Doctor' : 'Pending Verification'}
                    </Badge>
                  ) : null}
                </div>
              </CardBody>
            </Card>
          </div>
        </ModalBody>
        <ModalFooter>
          <Button color="secondary" onClick={handleCloseDoctorModal}>
            Close
          </Button>
        </ModalFooter>
      </Modal>
    );
  };

  if (loading) {
    return (
      <div className="user-history-page">
        <div className="container-fluid">
          <Card className="text-center">
            <CardBody className="py-5">
              <Spinner color="primary" style={{ width: '3rem', height: '3rem' }} />
              <h4 className="mt-3 text-muted">Loading Your History...</h4>
            </CardBody>
          </Card>
        </div>
      </div>
    );
  }

  return (
    <div className="user-history-page">
      <div className="container-fluid">
        <Card className="shadow-sm">
          <CardHeader className="bg-primary text-white">
            <h2 className="mb-0">
              <FontAwesomeIcon icon="history" className="me-2" />
              Your Medical History
            </h2>
          </CardHeader>
          <CardBody className="p-0">
            <Nav tabs className="border-bottom-0">
              <NavItem>
                <NavLink
                  className={classnames({ active: activeTab === 'symptom-searches' })}
                  onClick={() => toggleTab('symptom-searches')}
                  style={{ cursor: 'pointer' }}
                >
                  <FontAwesomeIcon icon="stethoscope" className="me-2" />
                  Symptom Searches ({symptomSearches.length})
                </NavLink>
              </NavItem>
              <NavItem>
                <NavLink
                  className={classnames({ active: activeTab === 'doctor-views' })}
                  onClick={() => toggleTab('doctor-views')}
                  style={{ cursor: 'pointer' }}
                >
                  <FontAwesomeIcon icon="user-md" className="me-2" />
                  Doctor Views ({doctorViews.length})
                </NavLink>
              </NavItem>
            </Nav>

            <TabContent activeTab={activeTab} className="p-4">
              <TabPane tabId="symptom-searches">{renderSymptomSearches()}</TabPane>
              <TabPane tabId="doctor-views">{renderDoctorViews()}</TabPane>
            </TabContent>
          </CardBody>
        </Card>

        {error && (
          <Card className="mt-4">
            <CardBody>
              <Alert color="danger" className="mb-0">
                <FontAwesomeIcon icon="exclamation-triangle" className="me-2" />
                {error}
              </Alert>
            </CardBody>
          </Card>
        )}
      </div>

      {/* Doctor Details Modal */}
      {renderDoctorModal()}
    </div>
  );
};

export default UserHistory;
