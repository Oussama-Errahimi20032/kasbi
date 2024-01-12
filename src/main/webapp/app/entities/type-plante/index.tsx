import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TypePlante from './type-plante';
import TypePlanteDetail from './type-plante-detail';
import TypePlanteUpdate from './type-plante-update';
import TypePlanteDeleteDialog from './type-plante-delete-dialog';

const TypePlanteRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TypePlante />} />
    <Route path="new" element={<TypePlanteUpdate />} />
    <Route path=":id">
      <Route index element={<TypePlanteDetail />} />
      <Route path="edit" element={<TypePlanteUpdate />} />
      <Route path="delete" element={<TypePlanteDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TypePlanteRoutes;
