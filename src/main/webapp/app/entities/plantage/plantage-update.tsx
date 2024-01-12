import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IPlante } from 'app/shared/model/plante.model';
import { getEntities as getPlantes } from 'app/entities/plante/plante.reducer';
import { IParcelle } from 'app/shared/model/parcelle.model';
import { getEntities as getParcelles } from 'app/entities/parcelle/parcelle.reducer';
import { IPlantage } from 'app/shared/model/plantage.model';
import { getEntity, updateEntity, createEntity, reset } from './plantage.reducer';

export const PlantageUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const plantes = useAppSelector(state => state.plante.entities);
  const parcelles = useAppSelector(state => state.parcelle.entities);
  const plantageEntity = useAppSelector(state => state.plantage.entity);
  const loading = useAppSelector(state => state.plantage.loading);
  const updating = useAppSelector(state => state.plantage.updating);
  const updateSuccess = useAppSelector(state => state.plantage.updateSuccess);

  const handleClose = () => {
    navigate('/plantage');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getPlantes({}));
    dispatch(getParcelles({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  // eslint-disable-next-line complexity
  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.nombre !== undefined && typeof values.nombre !== 'number') {
      values.nombre = Number(values.nombre);
    }

    const entity = {
      ...plantageEntity,
      ...values,
      planteLibelle: plantes.find(it => it.id.toString() === values.planteLibelle.toString()),
      parcelleLibelle: parcelles.find(it => it.id.toString() === values.parcelleLibelle.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...plantageEntity,
          planteLibelle: plantageEntity?.planteLibelle?.id,
          parcelleLibelle: plantageEntity?.parcelleLibelle?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="pharmaGesApp.plantage.home.createOrEditLabel" data-cy="PlantageCreateUpdateHeading">
            <Translate contentKey="pharmaGesApp.plantage.home.createOrEditLabel">Create or edit a Plantage</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="plantage-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField label={translate('pharmaGesApp.plantage.date')} id="plantage-date" name="date" data-cy="date" type="date" />
              <ValidatedField
                label={translate('pharmaGesApp.plantage.nombre')}
                id="plantage-nombre"
                name="nombre"
                data-cy="nombre"
                type="text"
              />
              <ValidatedField
                id="plantage-planteLibelle"
                name="planteLibelle"
                data-cy="planteLibelle"
                label={translate('pharmaGesApp.plantage.planteLibelle')}
                type="select"
              >
                <option value="" key="0" />
                {plantes
                  ? plantes.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="plantage-parcelleLibelle"
                name="parcelleLibelle"
                data-cy="parcelleLibelle"
                label={translate('pharmaGesApp.plantage.parcelleLibelle')}
                type="select"
              >
                <option value="" key="0" />
                {parcelles
                  ? parcelles.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/plantage" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default PlantageUpdate;
