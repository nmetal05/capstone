import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AppUserProfile from './app-user-profile';
import AppUserProfileDetail from './app-user-profile-detail';
import AppUserProfileUpdate from './app-user-profile-update';
import AppUserProfileDeleteDialog from './app-user-profile-delete-dialog';

const AppUserProfileRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<AppUserProfile />} />
    <Route path="new" element={<AppUserProfileUpdate />} />
    <Route path=":id">
      <Route index element={<AppUserProfileDetail />} />
      <Route path="edit" element={<AppUserProfileUpdate />} />
      <Route path="delete" element={<AppUserProfileDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AppUserProfileRoutes;
