import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import SymptomSearch from './symptom-search';
import SymptomSearchDetail from './symptom-search-detail';
import SymptomSearchUpdate from './symptom-search-update';
import SymptomSearchDeleteDialog from './symptom-search-delete-dialog';

const SymptomSearchRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<SymptomSearch />} />
    <Route path="new" element={<SymptomSearchUpdate />} />
    <Route path=":id">
      <Route index element={<SymptomSearchDetail />} />
      <Route path="edit" element={<SymptomSearchUpdate />} />
      <Route path="delete" element={<SymptomSearchDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SymptomSearchRoutes;
