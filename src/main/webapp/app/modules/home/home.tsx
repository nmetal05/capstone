import './home.scss';

import React, { useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { Row, Col } from 'reactstrap';

import { REDIRECT_URL } from 'app/shared/util/url-utils';
import { useAppSelector } from 'app/config/store';
import { AUTHORITIES } from 'app/config/constants';

import HeroSection from './components/HeroSection';
import AuthCard from './components/AuthCard';
import SymptomAnalysis from './components/SymptomAnalysis';
import FindDoctors from './components/FindDoctors';
import DoctorDashboard from './components/DoctorDashboard';

export const Home = () => {
  const account = useAppSelector(state => state.authentication.account);
  const pageLocation = useLocation();

  useEffect(() => {
    const redirectURL = localStorage.getItem(REDIRECT_URL);
    if (redirectURL) {
      localStorage.removeItem(REDIRECT_URL);
      location.href = `${location.origin}${redirectURL}`;
    }
  }, []);

  const isDoctor = account?.authorities?.includes(AUTHORITIES.DOCTOR);
  const isAdminOrAppUser = account?.authorities?.some(auth => auth === AUTHORITIES.ADMIN || auth === AUTHORITIES.APP_USER);

  const renderServicesGrid = () => {
    // If user is a doctor, show doctor-specific content
    if (isDoctor) {
      return (
        <div className="services-grid mt-5">
          <Row>
            <Col md="12" className="mb-4">
              <DoctorDashboard />
            </Col>
          </Row>
        </div>
      );
    }

    // For admin/app_user or guests, show the original components
    return (
      <div className="services-grid mt-5">
        <Row>
          <Col md="6" className="mb-4">
            <SymptomAnalysis />
          </Col>
          <Col md="6" className="mb-4">
            <FindDoctors />
          </Col>
        </Row>
      </div>
    );
  };

  return (
    <div className="home-container">
      <Row className="justify-content-center">
        <Col md="12" lg="10" className="home-content">
          <HeroSection />
          <AuthCard account={account} />
          {renderServicesGrid()}
        </Col>
      </Row>
    </div>
  );
};

export default Home;
