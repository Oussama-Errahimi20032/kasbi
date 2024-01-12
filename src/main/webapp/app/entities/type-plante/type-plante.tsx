import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortUp, faSortDown } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, SORT } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './type-plante.reducer';

export const TypePlante = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const typePlanteList = useAppSelector(state => state.typePlante.entities);
  const loading = useAppSelector(state => state.typePlante.loading);

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
      <h2 id="type-plante-heading" data-cy="TypePlanteHeading">
        <Translate contentKey="pharmaGesApp.typePlante.home.title">Type Plantes</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="pharmaGesApp.typePlante.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/type-plante/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="pharmaGesApp.typePlante.home.createLabel">Create new Type Plante</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {typePlanteList && typePlanteList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="pharmaGesApp.typePlante.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('name')}>
                  <Translate contentKey="pharmaGesApp.typePlante.name">Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th className="hand" onClick={sort('humiditeMax')}>
                  <Translate contentKey="pharmaGesApp.typePlante.humiditeMax">Humidite Max</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('humiditeMax')} />
                </th>
                <th className="hand" onClick={sort('humiditeMin')}>
                  <Translate contentKey="pharmaGesApp.typePlante.humiditeMin">Humidite Min</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('humiditeMin')} />
                </th>
                <th className="hand" onClick={sort('temperature')}>
                  <Translate contentKey="pharmaGesApp.typePlante.temperature">Temperature</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('temperature')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {typePlanteList.map((typePlante, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/type-plante/${typePlante.id}`} color="link" size="sm">
                      {typePlante.id}
                    </Button>
                  </td>
                  <td>{typePlante.name}</td>
                  <td>{typePlante.humiditeMax}</td>
                  <td>{typePlante.humiditeMin}</td>
                  <td>{typePlante.temperature}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/type-plante/${typePlante.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/type-plante/${typePlante.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/type-plante/${typePlante.id}/delete`)}
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
              <Translate contentKey="pharmaGesApp.typePlante.home.notFound">No Type Plantes found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default TypePlante;
