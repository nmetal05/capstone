/* app/modules/profile/ChooseProfileType.tsx */
import React, { useState } from 'react';
import { Button, Card, Alert } from 'reactstrap';
import { useNavigate, useLocation } from 'react-router-dom';

import { useAppDispatch } from 'app/config/store';
import { setUserRole, setSelectedRole } from 'app/shared/reducers/authentication';
import { AUTHORITIES } from 'app/config/constants';

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
    <div className="p-5" style={{ maxWidth: '800px', margin: '0 auto' }}>
      <h2 className="text-center mb-4">Choose Your Profile Type</h2>

      {error && (
        <Alert color="danger" className="text-center">
          {error}
        </Alert>
      )}

      <div className="d-flex justify-content-center gap-4 flex-wrap">
        {/* Regular user */}
        <Card className="p-4 text-center" style={{ width: '300px' }}>
          <h3>Regular User</h3>
          <p className="mb-4">Create a standard user account to browse doctors and services</p>
          <Button color="success" disabled={loading} onClick={() => handleRoleSelect(AUTHORITIES.APP_USER)}>
            {loading ? 'Processing…' : 'Select'}
          </Button>
        </Card>

        {/* Doctor */}
        <Card className="p-4 text-center" style={{ width: '300px' }}>
          <h3>Doctor</h3>
          <p className="mb-4">Create a doctor profile to offer medical services</p>
          <Button color="primary" onClick={() => handleRoleSelect(AUTHORITIES.DOCTOR)}>
            Select
          </Button>
        </Card>
      </div>
    </div>
  );
};

export default ChooseProfileType;
