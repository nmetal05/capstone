import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import DoctorViewHistory from './doctor-view-history';
import DoctorViewHistoryDetail from './doctor-view-history-detail';
import DoctorViewHistoryUpdate from './doctor-view-history-update';
import DoctorViewHistoryDeleteDialog from './doctor-view-history-delete-dialog';

const DoctorViewHistoryRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<DoctorViewHistory />} />
    <Route path="new" element={<DoctorViewHistoryUpdate />} />
    <Route path=":id">
      <Route index element={<DoctorViewHistoryDetail />} />
      <Route path="edit" element={<DoctorViewHistoryUpdate />} />
      <Route path="delete" element={<DoctorViewHistoryDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default DoctorViewHistoryRoutes;
