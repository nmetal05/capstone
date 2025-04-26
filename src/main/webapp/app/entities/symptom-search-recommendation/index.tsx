import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import SymptomSearchRecommendation from './symptom-search-recommendation';
import SymptomSearchRecommendationDetail from './symptom-search-recommendation-detail';
import SymptomSearchRecommendationUpdate from './symptom-search-recommendation-update';
import SymptomSearchRecommendationDeleteDialog from './symptom-search-recommendation-delete-dialog';

const SymptomSearchRecommendationRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<SymptomSearchRecommendation />} />
    <Route path="new" element={<SymptomSearchRecommendationUpdate />} />
    <Route path=":id">
      <Route index element={<SymptomSearchRecommendationDetail />} />
      <Route path="edit" element={<SymptomSearchRecommendationUpdate />} />
      <Route path="delete" element={<SymptomSearchRecommendationDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SymptomSearchRecommendationRoutes;
