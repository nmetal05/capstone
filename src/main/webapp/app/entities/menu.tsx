import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/app-user-profile">
        <Translate contentKey="global.menu.entities.appUserProfile" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/doctor-profile">
        <Translate contentKey="global.menu.entities.doctorProfile" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/specialization">
        <Translate contentKey="global.menu.entities.specialization" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/symptom-search">
        <Translate contentKey="global.menu.entities.symptomSearch" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/symptom-search-recommendation">
        <Translate contentKey="global.menu.entities.symptomSearchRecommendation" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/doctor-document">
        <Translate contentKey="global.menu.entities.doctorDocument" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/guest-session">
        <Translate contentKey="global.menu.entities.guestSession" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/doctor-view-history">
        <Translate contentKey="global.menu.entities.doctorViewHistory" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
