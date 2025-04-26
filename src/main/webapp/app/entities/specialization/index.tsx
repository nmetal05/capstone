import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Specialization from './specialization';
import SpecializationDetail from './specialization-detail';
import SpecializationUpdate from './specialization-update';
import SpecializationDeleteDialog from './specialization-delete-dialog';

const SpecializationRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Specialization />} />
    <Route path="new" element={<SpecializationUpdate />} />
    <Route path=":id">
      <Route index element={<SpecializationDetail />} />
      <Route path="edit" element={<SpecializationUpdate />} />
      <Route path="delete" element={<SpecializationDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SpecializationRoutes;
