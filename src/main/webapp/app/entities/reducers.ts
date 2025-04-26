import appUserProfile from 'app/entities/app-user-profile/app-user-profile.reducer';
import doctorProfile from 'app/entities/doctor-profile/doctor-profile.reducer';
import specialization from 'app/entities/specialization/specialization.reducer';
import symptomSearch from 'app/entities/symptom-search/symptom-search.reducer';
import symptomSearchRecommendation from 'app/entities/symptom-search-recommendation/symptom-search-recommendation.reducer';
import doctorDocument from 'app/entities/doctor-document/doctor-document.reducer';
import guestSession from 'app/entities/guest-session/guest-session.reducer';
import doctorViewHistory from 'app/entities/doctor-view-history/doctor-view-history.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  appUserProfile,
  doctorProfile,
  specialization,
  symptomSearch,
  symptomSearchRecommendation,
  doctorDocument,
  guestSession,
  doctorViewHistory,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
