import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import DoctorProfile from './doctor-profile';
import DoctorProfileDetail from './doctor-profile-detail';
import DoctorProfileUpdate from './doctor-profile-update';
import DoctorProfileDeleteDialog from './doctor-profile-delete-dialog';

const DoctorProfileRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<DoctorProfile />} />
    <Route path="new" element={<DoctorProfileUpdate />} />
    <Route path=":id">
      <Route index element={<DoctorProfileDetail />} />
      <Route path="edit" element={<DoctorProfileUpdate />} />
      <Route path="delete" element={<DoctorProfileDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default DoctorProfileRoutes;
