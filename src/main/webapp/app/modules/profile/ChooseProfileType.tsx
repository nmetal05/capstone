/* app/modules/profile/ChooseProfileType.tsx */
import React, { useState } from 'react';
import { Button, Card, Alert } from 'reactstrap';
import { useNavigate, useLocation } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { Translate } from 'react-jhipster';
import { useAppDispatch } from 'app/config/store';
import { setUserRole, setSelectedRole } from 'app/shared/reducers/authentication';
import { AUTHORITIES } from 'app/config/constants';
import './choose-profile-type.scss';

/* helper: "ROLE_FOO" → "FOO" */
const authorityToApiEnum = (a: string) => a.replace(/^ROLE_/, '');

const ChooseProfileType = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const location = useLocation();

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const from = location.state?.from?.pathname || '/';

  const handleRoleSelect = async (authority: string) => {
    if (authority === AUTHORITIES.DOCTOR) {
      /* remember choice; onboarding not finished yet */
      dispatch(setSelectedRole(AUTHORITIES.DOCTOR));
      navigate('/complete-doctor-profile', { replace: true });
      return;
    }

    /* ---- Regular APP_USER branch ---- */
    setLoading(true);
    setError('');
    try {
      const apiRole = authorityToApiEnum(authority); // APP_USER
      await dispatch(setUserRole(apiRole)).unwrap();
      navigate(from, { replace: true });
    } catch (err: any) {
      setError(err?.message || 'Failed to select profile type');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="choose-profile-container">
      <div className="choose-profile-content">
        <div className="text-center mb-5">
          <div className="welcome-icon mb-3">
            <FontAwesomeIcon icon="user-plus" />
          </div>
          <h1 className="welcome-title">
            <Translate contentKey="ChooseProfileType.title">Choose Your Profile Type</Translate>
          </h1>
          <p className="welcome-subtitle">Select the type of account that best describes your role</p>
        </div>

        {error && (
          <Alert color="danger" className="error-alert">
            <FontAwesomeIcon icon="exclamation-triangle" className="me-2" />
            {error}
          </Alert>
        )}

        <div className="profile-cards">
          {/* Regular User Card */}
          <Card className="profile-card user-card">
            <div className="card-body">
              <div className="profile-icon user-icon">
                <FontAwesomeIcon icon="user" />
              </div>
              <h3 className="profile-title">
                <Translate contentKey="ChooseProfileType.regular_user">Regular User</Translate>
              </h3>
              <p className="profile-description">
                <Translate contentKey="ChooseProfileType.Signup">Create a standard user account to browse doctors and services</Translate>
              </p>
              <div className="profile-features">
                <div className="feature-item">
                  <FontAwesomeIcon icon="search" className="feature-icon" />
                  <span>Find doctors near you</span>
                </div>
                <div className="feature-item">
                  <FontAwesomeIcon icon="heart" className="feature-icon" />
                  <span>Access health services</span>
                </div>
                <div className="feature-item">
                  <FontAwesomeIcon icon="calendar-alt" className="feature-icon" />
                  <span>Book appointments</span>
                </div>
              </div>
              <Button
                color="success"
                size="lg"
                className="profile-button"
                disabled={loading}
                onClick={() => handleRoleSelect(AUTHORITIES.APP_USER)}
              >
                {loading ? (
                  <>
                    <FontAwesomeIcon icon="spinner" spin className="me-2" />
                    Processing...
                  </>
                ) : (
                  <>
                    <FontAwesomeIcon icon="check" className="me-2" />
                    <Translate contentKey="ChooseProfileType.select">Select</Translate>
                  </>
                )}
              </Button>
            </div>
          </Card>

          {/* Doctor Card */}
          <Card className="profile-card doctor-card">
            <div className="card-body">
              <div className="profile-icon doctor-icon">
                <FontAwesomeIcon icon="user-md" />
              </div>
              <h3 className="profile-title">
                <Translate contentKey="ChooseProfileType.doctor">Doctor</Translate>
              </h3>
              <p className="profile-description">
                <Translate contentKey="ChooseProfileType.DoctorSignup">Create a doctor profile to offer medical services</Translate>
              </p>
              <div className="profile-features">
                <div className="feature-item">
                  <FontAwesomeIcon icon="stethoscope" className="feature-icon" />
                  <span>Manage your practice</span>
                </div>
                <div className="feature-item">
                  <FontAwesomeIcon icon="calendar-check" className="feature-icon" />
                  <span>Set availability</span>
                </div>
                <div className="feature-item">
                  <FontAwesomeIcon icon="file-medical" className="feature-icon" />
                  <span>Submit credentials</span>
                </div>
              </div>
              <Button color="primary" size="lg" className="profile-button" onClick={() => handleRoleSelect(AUTHORITIES.DOCTOR)}>
                <FontAwesomeIcon icon="arrow-right" className="me-2" />
                <Translate contentKey="ChooseProfileType.select">Select</Translate>
              </Button>
            </div>
          </Card>
        </div>

        <div className="text-center mt-4">
          <small className="text-muted">You can always update your profile information later</small>
        </div>
      </div>
    </div>
  );
};

export default ChooseProfileType;
