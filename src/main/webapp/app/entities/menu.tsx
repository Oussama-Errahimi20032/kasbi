import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/app-user">
        <Translate contentKey="global.menu.entities.appUser" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/parcelle">
        <Translate contentKey="global.menu.entities.parcelle" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/plantage">
        <Translate contentKey="global.menu.entities.plantage" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/plante">
        <Translate contentKey="global.menu.entities.plante" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/ferme">
        <Translate contentKey="global.menu.entities.ferme" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/type-plante">
        <Translate contentKey="global.menu.entities.typePlante" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/admin">
        <Translate contentKey="global.menu.entities.admin" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
