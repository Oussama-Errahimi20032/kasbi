import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AppUser from './app-user';
import Parcelle from './parcelle';
import Plantage from './plantage';
import Plante from './plante';
import Ferme from './ferme';
import TypePlante from './type-plante';
import Admin from './admin';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="app-user/*" element={<AppUser />} />
        <Route path="parcelle/*" element={<Parcelle />} />
        <Route path="plantage/*" element={<Plantage />} />
        <Route path="plante/*" element={<Plante />} />
        <Route path="ferme/*" element={<Ferme />} />
        <Route path="type-plante/*" element={<TypePlante />} />
        <Route path="admin/*" element={<Admin />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
