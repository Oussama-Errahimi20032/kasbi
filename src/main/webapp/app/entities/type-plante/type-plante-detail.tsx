import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './type-plante.reducer';

export const TypePlanteDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const typePlanteEntity = useAppSelector(state => state.typePlante.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="typePlanteDetailsHeading">
          <Translate contentKey="pharmaGesApp.typePlante.detail.title">TypePlante</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{typePlanteEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="pharmaGesApp.typePlante.name">Name</Translate>
            </span>
          </dt>
          <dd>{typePlanteEntity.name}</dd>
          <dt>
            <span id="humiditeMax">
              <Translate contentKey="pharmaGesApp.typePlante.humiditeMax">Humidite Max</Translate>
            </span>
          </dt>
          <dd>{typePlanteEntity.humiditeMax}</dd>
          <dt>
            <span id="humiditeMin">
              <Translate contentKey="pharmaGesApp.typePlante.humiditeMin">Humidite Min</Translate>
            </span>
          </dt>
          <dd>{typePlanteEntity.humiditeMin}</dd>
          <dt>
            <span id="temperature">
              <Translate contentKey="pharmaGesApp.typePlante.temperature">Temperature</Translate>
            </span>
          </dt>
          <dd>{typePlanteEntity.temperature}</dd>
        </dl>
        <Button tag={Link} to="/type-plante" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/type-plante/${typePlanteEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TypePlanteDetail;
