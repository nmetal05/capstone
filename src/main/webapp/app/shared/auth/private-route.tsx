import React from 'react';
import { Navigate, PathRouteProps, useLocation } from 'react-router-dom';
import { Translate } from 'react-jhipster';
import { useAppSelector } from 'app/config/store';
import ErrorBoundary from 'app/shared/error/error-boundary';
import { AUTHORITIES } from 'app/config/constants';

interface IOwnProps extends PathRouteProps {
  hasAnyAuthorities?: string[];
  children: React.ReactNode;
}

export const PrivateRoute = ({ children, hasAnyAuthorities = [], ...rest }: IOwnProps) => {
  const { isAuthenticated, sessionHasBeenFetched, account, roleSelectionCompleted } = useAppSelector(state => state.authentication);

  const { hasDoctorProfile, doctorProfileComplete } = useAppSelector(state => state.profile);

  const location = useLocation();
  if (!children) throw new Error(`A component needs to be specified for private route for path ${(rest as any).path}`);
  if (!sessionHasBeenFetched) return <div />;

  const isAdmin = account.authorities?.includes(AUTHORITIES.ADMIN);

  /* ----- new logic: profile flags alone are enough ----- */
  const onboardingFinished = roleSelectionCompleted || hasDoctorProfile || doctorProfileComplete;

  const needsRoleSelection =
    isAuthenticated &&
    !isAdmin &&
    !onboardingFinished &&
    location.pathname !== '/choose-profile' &&
    location.pathname !== '/complete-doctor-profile';

  const from = location.state?.from || { pathname: '/' };

  if (needsRoleSelection) {
    return <Navigate to="/choose-profile" state={{ from }} replace />;
  }

  if (isAuthenticated) {
    if (isAdmin) return <ErrorBoundary>{children}</ErrorBoundary>;

    /* when onboarding is finished every non-admin may enter */
    return <ErrorBoundary>{children}</ErrorBoundary>;
  }

  return <Navigate to={{ pathname: '/sign-in', search: location.search }} replace state={{ from: location }} />;
};

export default PrivateRoute;
