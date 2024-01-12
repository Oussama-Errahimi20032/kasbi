import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './plante.reducer';

export const PlanteDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const planteEntity = useAppSelector(state => state.plante.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="planteDetailsHeading">
          <Translate contentKey="pharmaGesApp.plante.detail.title">Plante</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{planteEntity.id}</dd>
          <dt>
            <span id="libelle">
              <Translate contentKey="pharmaGesApp.plante.libelle">Libelle</Translate>
            </span>
          </dt>
          <dd>{planteEntity.libelle}</dd>
          <dt>
            <span id="racine">
              <Translate contentKey="pharmaGesApp.plante.racine">Racine</Translate>
            </span>
          </dt>
          <dd>{planteEntity.racine}</dd>
          <dt>
            <span id="image">
              <Translate contentKey="pharmaGesApp.plante.image">Image</Translate>
            </span>
          </dt>
          <dd>{planteEntity.image}</dd>
          <dt>
            <Translate contentKey="pharmaGesApp.plante.typePlante">Type Plante</Translate>
          </dt>
          <dd>{planteEntity.typePlante ? planteEntity.typePlante.id : ''}</dd>
          <dt>
            <Translate contentKey="pharmaGesApp.plante.nom">Nom</Translate>
          </dt>
          <dd>{planteEntity.nom ? planteEntity.nom.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/plante" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/plante/${planteEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default PlanteDetail;
