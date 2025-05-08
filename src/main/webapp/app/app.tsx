/* app/app.tsx */
import 'react-toastify/dist/ReactToastify.css';
import './app.scss';
import 'app/config/dayjs';

import React, { useEffect } from 'react';
import { BrowserRouter, useLocation, useNavigate } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import { Card } from 'reactstrap';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getSession } from 'app/shared/reducers/authentication';
import { getProfile } from 'app/shared/reducers/application-profile';
import { fetchProfileStatus } from 'app/shared/reducers/profile';
import Header from 'app/shared/layout/header/header';
import Footer from 'app/shared/layout/footer/footer';
import { AUTHORITIES } from 'app/config/constants';
import ErrorBoundary from 'app/shared/error/error-boundary';
import AppRoutes from 'app/routes';
import { setTextDirection } from './config/translation';

const baseHref = document.querySelector('base')?.getAttribute('href')?.replace(/\/$/, '') || '';

const AppContent = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const location = useLocation();

  /* Redux state */
  const currentLocale = useAppSelector(state => state.locale.currentLocale);
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);
  const isAdmin = useAppSelector(state => state.authentication.account.authorities?.includes(AUTHORITIES.ADMIN));
  const roleSelectionCompleted = useAppSelector(state => state.authentication.roleSelectionCompleted);
  const selectedRole = useAppSelector(state => state.authentication.selectedRole);

  const { loading: profileLoading, hasDoctorProfile, doctorProfileComplete } = useAppSelector(state => state.profile);

  const ribbonEnv = useAppSelector(state => state.applicationProfile.ribbonEnv);
  const isInProduction = useAppSelector(state => state.applicationProfile.inProduction);
  const isOpenAPIEnabled = useAppSelector(state => state.applicationProfile.isOpenAPIEnabled);

  /* Bootstrapping */
  useEffect(() => {
    dispatch(getSession());
    dispatch(getProfile());
  }, [dispatch]);

  useEffect(() => {
    if (isAuthenticated) dispatch(fetchProfileStatus());
  }, [isAuthenticated, dispatch]);

  /* Redirect rules */
  useEffect(() => {
    if (!isAuthenticated || profileLoading || isAdmin) return;

    const path = location.pathname;
    const isOnboardPage = path === '/choose-profile' || path === '/complete-doctor-profile';

    /* ----- new logic: profile flags alone are enough ----- */
    const onboardingFinished = roleSelectionCompleted || hasDoctorProfile || doctorProfileComplete;

    /* 1) finished onboarding but still on an onboarding page → home */
    if (onboardingFinished && isOnboardPage) {
      navigate('/', { replace: true });
      return;
    }

    /* 2) must do onboarding but is outside onboarding pages → push */
    if (!onboardingFinished && !isOnboardPage) {
      navigate('/choose-profile', { replace: true });
      return;
    }

    /* 3) doctor profile not complete yet → stay / go to form page */
    if (selectedRole === AUTHORITIES.DOCTOR && !doctorProfileComplete && path !== '/complete-doctor-profile') {
      navigate('/complete-doctor-profile', { replace: true });
    }
  }, [
    isAuthenticated,
    profileLoading,
    location.pathname,
    isAdmin,
    roleSelectionCompleted,
    hasDoctorProfile,
    doctorProfileComplete,
    selectedRole,
    navigate,
  ]);

  /* Text direction */
  useEffect(() => {
    setTextDirection(currentLocale);
  }, [currentLocale]);

  /* Render */
  return (
    <div className="app-container" style={{ paddingTop: '60px' }}>
      <ToastContainer position="top-left" className="toastify-container" toastClassName="toastify-toast" />
      <ErrorBoundary>
        <Header
          isAuthenticated={isAuthenticated}
          isAdmin={isAdmin}
          currentLocale={currentLocale}
          ribbonEnv={ribbonEnv}
          isInProduction={isInProduction}
          isOpenAPIEnabled={isOpenAPIEnabled}
        />
      </ErrorBoundary>

      <div className="container-fluid view-container" id="app-view-container">
        <Card className="jh-card" style={{ minHeight: 'calc(100vh - 120px)' }}>
          <ErrorBoundary>
            {profileLoading ? (
              <div className="d-flex justify-content-center align-items-center" style={{ height: '100%' }}>
                <div className="spinner-border text-primary" role="status" />
              </div>
            ) : (
              <AppRoutes />
            )}
          </ErrorBoundary>
        </Card>
        <Footer />
      </div>
    </div>
  );
};

export const App = () => (
  <BrowserRouter basename={baseHref}>
    <AppContent />
  </BrowserRouter>
);

export default App;
