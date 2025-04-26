import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import DoctorDocument from './doctor-document';
import DoctorDocumentDetail from './doctor-document-detail';
import DoctorDocumentUpdate from './doctor-document-update';
import DoctorDocumentDeleteDialog from './doctor-document-delete-dialog';

const DoctorDocumentRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<DoctorDocument />} />
    <Route path="new" element={<DoctorDocumentUpdate />} />
    <Route path=":id">
      <Route index element={<DoctorDocumentDetail />} />
      <Route path="edit" element={<DoctorDocumentUpdate />} />
      <Route path="delete" element={<DoctorDocumentDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default DoctorDocumentRoutes;
