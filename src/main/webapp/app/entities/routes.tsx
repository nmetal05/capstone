import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AppUserProfile from './app-user-profile';
import DoctorProfile from './doctor-profile';
import Specialization from './specialization';
import SymptomSearch from './symptom-search';
import SymptomSearchRecommendation from './symptom-search-recommendation';
import DoctorDocument from './doctor-document';
import GuestSession from './guest-session';
import DoctorViewHistory from './doctor-view-history';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="app-user-profile/*" element={<AppUserProfile />} />
        <Route path="doctor-profile/*" element={<DoctorProfile />} />
        <Route path="specialization/*" element={<Specialization />} />
        <Route path="symptom-search/*" element={<SymptomSearch />} />
        <Route path="symptom-search-recommendation/*" element={<SymptomSearchRecommendation />} />
        <Route path="doctor-document/*" element={<DoctorDocument />} />
        <Route path="guest-session/*" element={<GuestSession />} />
        <Route path="doctor-view-history/*" element={<DoctorViewHistory />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
