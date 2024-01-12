import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortUp, faSortDown } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, SORT } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './plante.reducer';

export const Plante = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const planteList = useAppSelector(state => state.plante.entities);
  const loading = useAppSelector(state => state.plante.loading);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const order = sortState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    } else {
      return order === ASC ? faSortUp : faSortDown;
    }
  };

  return (
    <div>
      <h2 id="plante-heading" data-cy="PlanteHeading">
        <Translate contentKey="pharmaGesApp.plante.home.title">Plantes</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="pharmaGesApp.plante.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/plante/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="pharmaGesApp.plante.home.createLabel">Create new Plante</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {planteList && planteList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="pharmaGesApp.plante.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('libelle')}>
                  <Translate contentKey="pharmaGesApp.plante.libelle">Libelle</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('libelle')} />
                </th>
                <th className="hand" onClick={sort('racine')}>
                  <Translate contentKey="pharmaGesApp.plante.racine">Racine</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('racine')} />
                </th>
                <th className="hand" onClick={sort('image')}>
                  <Translate contentKey="pharmaGesApp.plante.image">Image</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('image')} />
                </th>
                <th>
                  <Translate contentKey="pharmaGesApp.plante.typePlante">Type Plante</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="pharmaGesApp.plante.nom">Nom</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {planteList.map((plante, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/plante/${plante.id}`} color="link" size="sm">
                      {plante.id}
                    </Button>
                  </td>
                  <td>{plante.libelle}</td>
                  <td>{plante.racine}</td>
                  <td>{plante.image}</td>
                  <td>{plante.typePlante ? <Link to={`/type-plante/${plante.typePlante.id}`}>{plante.typePlante.id}</Link> : ''}</td>
                  <td>{plante.nom ? <Link to={`/type-plante/${plante.nom.id}`}>{plante.nom.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/plante/${plante.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/plante/${plante.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/plante/${plante.id}/delete`)}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="pharmaGesApp.plante.home.notFound">No Plantes found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Plante;
