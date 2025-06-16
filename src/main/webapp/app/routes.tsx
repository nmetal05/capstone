import React from 'react';
import { Route } from 'react-router-dom';
import Loadable from 'react-loadable';
import { NearbyDoctors } from './modules/geolocalisation/nearby-doctors';

import { AUTHORITIES } from 'app/config/constants';
import PrivateRoute from 'app/shared/auth/private-route';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

const loading = <div>loading …</div>;

const Admin = Loadable({ loader: () => import('app/modules/administration'), loading: () => loading });
const ChooseProfileType = Loadable({ loader: () => import('app/modules/profile/ChooseProfileType'), loading: () => loading });
const CompleteDoctorProfile = Loadable({ loader: () => import('app/modules/profile/CompleteDoctorProfile'), loading: () => loading });
const LoginRedirect = Loadable({ loader: () => import('app/modules/login/login-redirect'), loading: () => loading });
const Logout = Loadable({ loader: () => import('app/modules/login/logout'), loading: () => loading });
const Home = Loadable({ loader: () => import('app/modules/home/home'), loading: () => loading });
const UserHistory = Loadable({ loader: () => import('app/modules/history/user-history'), loading: () => loading });
const AccountManage = Loadable({ loader: () => import('app/modules/account'), loading: () => loading });
const EntitiesRoutes = Loadable({ loader: () => import('app/entities/routes'), loading: () => loading });
const PageNotFound = Loadable({ loader: () => import('app/shared/error/page-not-found'), loading: () => loading });

const AppRoutes = () => (
  <div className="view-routes">
    <ErrorBoundaryRoutes>
      {/* Public / common pages */}
      <Route index element={<Home />} />
      <Route path="logout" element={<Logout />} />
      <Route path="sign-in" element={<LoginRedirect />} />
      <Route
        path="doctors/nearby"
        element={
          <PrivateRoute hasAnyAuthorities={[AUTHORITIES.USER, AUTHORITIES.ADMIN]}>
            <NearbyDoctors />
          </PrivateRoute>
        }
      />

      {/* User History */}
      <Route
        path="history"
        element={
          <PrivateRoute hasAnyAuthorities={[AUTHORITIES.USER, AUTHORITIES.ADMIN, AUTHORITIES.APP_USER]}>
            <UserHistory />
          </PrivateRoute>
        }
      />

      {/* Account Management */}
      <Route
        path="account/manage"
        element={
          <PrivateRoute hasAnyAuthorities={[AUTHORITIES.ADMIN, AUTHORITIES.DOCTOR, AUTHORITIES.APP_USER]}>
            <AccountManage />
          </PrivateRoute>
        }
      />

      {/* Administration */}
      <Route
        path="admin/*"
        element={
          <PrivateRoute hasAnyAuthorities={[AUTHORITIES.ADMIN]}>
            <Admin />
          </PrivateRoute>
        }
      />

      {/* On-boarding */}
      <Route
        path="choose-profile"
        element={
          <PrivateRoute>
            <ChooseProfileType />
          </PrivateRoute>
        }
      />
      {/* NOTE: no hasAnyAuthorities here – user is not a doctor yet */}
      <Route
        path="complete-doctor-profile"
        element={
          <PrivateRoute>
            <CompleteDoctorProfile />
          </PrivateRoute>
        }
      />

      {/* Authenticated area (entities, etc.) */}
      <Route
        path="*"
        element={
          <PrivateRoute hasAnyAuthorities={[AUTHORITIES.ADMIN]}>
            <EntitiesRoutes />
          </PrivateRoute>
        }
      />

      {/* Optional 404 inside authenticated zone */}
      <Route path="404" element={<PageNotFound />} />
    </ErrorBoundaryRoutes>
  </div>
);

export default AppRoutes;
