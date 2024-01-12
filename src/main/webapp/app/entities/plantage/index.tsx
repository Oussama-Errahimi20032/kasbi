import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Plantage from './plantage';
import PlantageDetail from './plantage-detail';
import PlantageUpdate from './plantage-update';
import PlantageDeleteDialog from './plantage-delete-dialog';

const PlantageRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Plantage />} />
    <Route path="new" element={<PlantageUpdate />} />
    <Route path=":id">
      <Route index element={<PlantageDetail />} />
      <Route path="edit" element={<PlantageUpdate />} />
      <Route path="delete" element={<PlantageDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default PlantageRoutes;
