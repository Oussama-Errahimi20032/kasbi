import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Parcelle from './parcelle';
import ParcelleDetail from './parcelle-detail';
import ParcelleUpdate from './parcelle-update';
import ParcelleDeleteDialog from './parcelle-delete-dialog';

const ParcelleRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Parcelle />} />
    <Route path="new" element={<ParcelleUpdate />} />
    <Route path=":id">
      <Route index element={<ParcelleDetail />} />
      <Route path="edit" element={<ParcelleUpdate />} />
      <Route path="delete" element={<ParcelleDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ParcelleRoutes;
