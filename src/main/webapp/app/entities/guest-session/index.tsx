import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import GuestSession from './guest-session';
import GuestSessionDetail from './guest-session-detail';
import GuestSessionUpdate from './guest-session-update';
import GuestSessionDeleteDialog from './guest-session-delete-dialog';

const GuestSessionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<GuestSession />} />
    <Route path="new" element={<GuestSessionUpdate />} />
    <Route path=":id">
      <Route index element={<GuestSessionDetail />} />
      <Route path="edit" element={<GuestSessionUpdate />} />
      <Route path="delete" element={<GuestSessionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default GuestSessionRoutes;
