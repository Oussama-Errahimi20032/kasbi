import appUser from 'app/entities/app-user/app-user.reducer';
import parcelle from 'app/entities/parcelle/parcelle.reducer';
import plantage from 'app/entities/plantage/plantage.reducer';
import plante from 'app/entities/plante/plante.reducer';
import ferme from 'app/entities/ferme/ferme.reducer';
import typePlante from 'app/entities/type-plante/type-plante.reducer';
import admin from 'app/entities/admin/admin.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  appUser,
  parcelle,
  plantage,
  plante,
  ferme,
  typePlante,
  admin,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
