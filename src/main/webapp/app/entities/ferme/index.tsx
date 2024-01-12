import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Ferme from './ferme';
import FermeDetail from './ferme-detail';
import FermeUpdate from './ferme-update';
import FermeDeleteDialog from './ferme-delete-dialog';

const FermeRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Ferme />} />
    <Route path="new" element={<FermeUpdate />} />
    <Route path=":id">
      <Route index element={<FermeDetail />} />
      <Route path="edit" element={<FermeUpdate />} />
      <Route path="delete" element={<FermeDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default FermeRoutes;
