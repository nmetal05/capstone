import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { Button, Alert } from 'reactstrap';

import { useAppDispatch } from 'app/config/store';
import { completeRoleSelection } from 'app/shared/reducers/authentication';
import { fetchProfileStatus, setDoctorProfileComplete, setHasDoctorProfile } from 'app/shared/reducers/profile';
import { AUTHORITIES } from 'app/config/constants';

const CompleteDoctorProfile = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  /* form fields */
  const [inpeCode, setInpeCode] = useState('');
  const [latitude, setLatitude] = useState('');
  const [longitude, setLongitude] = useState('');
  const [officeAddress, setOfficeAddress] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const submit = async () => {
    setSaving(true);
    setError(null);

    const payload: any = { type: 'DOCTOR' };
    if (inpeCode.trim()) payload.inpeCode = inpeCode.trim();
    if (officeAddress.trim()) payload.officeAddress = officeAddress.trim();
    if (phoneNumber.trim()) payload.phoneNumber = phoneNumber.trim();
    if (latitude) payload.latitude = +latitude;
    if (longitude) payload.longitude = +longitude;

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
      setError(e.response?.data?.message || 'Update failed');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="container mt-4" style={{ maxWidth: '600px' }}>
      <h2 className="mb-4">Complete Doctor Profile</h2>

      {error && (
        <Alert color="danger" className="text-center">
          {error}
        </Alert>
      )}

      <div className="mb-3">
        <input className="form-control mb-2" placeholder="INPE Code" value={inpeCode} onChange={e => setInpeCode(e.target.value)} />
        <input
          className="form-control mb-2"
          placeholder="Latitude"
          type="number"
          value={latitude}
          onChange={e => setLatitude(e.target.value)}
        />
        <input
          className="form-control mb-2"
          placeholder="Longitude"
          type="number"
          value={longitude}
          onChange={e => setLongitude(e.target.value)}
        />
        <input
          className="form-control mb-2"
          placeholder="Office Address"
          value={officeAddress}
          onChange={e => setOfficeAddress(e.target.value)}
        />
        <input
          className="form-control mb-2"
          placeholder="Phone Number"
          value={phoneNumber}
          onChange={e => setPhoneNumber(e.target.value)}
        />
      </div>

      <Button color="primary" onClick={submit} disabled={saving}>
        {saving ? 'Saving…' : 'Save'}
      </Button>
    </div>
  );
};

export default CompleteDoctorProfile;
