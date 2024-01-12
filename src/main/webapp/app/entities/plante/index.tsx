import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Plante from './plante';
import PlanteDetail from './plante-detail';
import PlanteUpdate from './plante-update';
import PlanteDeleteDialog from './plante-delete-dialog';

const PlanteRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Plante />} />
    <Route path="new" element={<PlanteUpdate />} />
    <Route path=":id">
      <Route index element={<PlanteDetail />} />
      <Route path="edit" element={<PlanteUpdate />} />
      <Route path="delete" element={<PlanteDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default PlanteRoutes;
