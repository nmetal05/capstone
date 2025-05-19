import React, { useEffect, useState } from 'react';
import { Card, Row, Col, Spinner } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Translate } from 'react-jhipster';
import axios from 'axios';
import BaseUserInfo from './base-user-info';
import DoctorProfile from './doctor-profile';
import { AUTHORITIES } from 'app/config/constants';
import './account-manage.scss';

const AccountManage = () => {
  const [account, setAccount] = useState(null);
  const [doctorProfile, setDoctorProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      try {
        // Fetch base account info
        const accountResponse = await axios.get('/api/account');
        setAccount(accountResponse.data);

        // Get profile status to check if user has a doctor profile
        const profileStatusResponse = await axios.get('/api/profile/status');
        const { hasDoctorProfile } = profileStatusResponse.data;

        // Check user authorities and fetch appropriate profile
        const authorities = accountResponse.data.authorities || [];
        if (authorities.includes(AUTHORITIES.DOCTOR) && hasDoctorProfile) {
          // Get all doctor profiles and find the one for the current user
          const doctorProfilesResponse = await axios.get('/api/doctor-profiles');
          const currentDoctorProfile = doctorProfilesResponse.data.find(profile => profile.internalUser?.id === accountResponse.data.id);
          if (currentDoctorProfile) {
            setDoctorProfile(currentDoctorProfile);
          }
        }
      } catch (err) {
        setError('account.manage.error');
        console.error('Error loading account:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) {
    return (
      <div className="text-center p-5">
        <Spinner color="primary" />
        <p className="mt-3">
          <Translate contentKey="account.manage.loading">Loading account information...</Translate>
        </p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="alert alert-danger m-3">
        <FontAwesomeIcon icon="exclamation-triangle" className="me-2" />
        <Translate contentKey={error}>Error loading account information</Translate>
      </div>
    );
  }

  return (
    <div className="account-manage">
      <h2 className="mb-4">
        <FontAwesomeIcon icon="user-cog" className="me-2" />
        <Translate contentKey="account.manage.title">Account Management</Translate>
      </h2>

      <Row>
        <Col md={12} className="mb-4">
          <Card>
            <BaseUserInfo account={account} />
          </Card>
        </Col>

        {doctorProfile && (
          <Col md={12} className="mb-4">
            <Card>
              <DoctorProfile profile={doctorProfile} />
            </Card>
          </Col>
        )}
      </Row>
    </div>
  );
};

export default AccountManage;
