import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './plantage.reducer';

export const PlantageDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const plantageEntity = useAppSelector(state => state.plantage.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="plantageDetailsHeading">
          <Translate contentKey="pharmaGesApp.plantage.detail.title">Plantage</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{plantageEntity.id}</dd>
          <dt>
            <span id="date">
              <Translate contentKey="pharmaGesApp.plantage.date">Date</Translate>
            </span>
          </dt>
          <dd>{plantageEntity.date ? <TextFormat value={plantageEntity.date} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="nombre">
              <Translate contentKey="pharmaGesApp.plantage.nombre">Nombre</Translate>
            </span>
          </dt>
          <dd>{plantageEntity.nombre}</dd>
          <dt>
            <Translate contentKey="pharmaGesApp.plantage.planteLibelle">Plante Libelle</Translate>
          </dt>
          <dd>{plantageEntity.planteLibelle ? plantageEntity.planteLibelle.id : ''}</dd>
          <dt>
            <Translate contentKey="pharmaGesApp.plantage.parcelleLibelle">Parcelle Libelle</Translate>
          </dt>
          <dd>{plantageEntity.parcelleLibelle ? plantageEntity.parcelleLibelle.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/plantage" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/plantage/${plantageEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default PlantageDetail;
