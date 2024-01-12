import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ITypePlante } from 'app/shared/model/type-plante.model';
import { getEntity, updateEntity, createEntity, reset } from './type-plante.reducer';

export const TypePlanteUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const typePlanteEntity = useAppSelector(state => state.typePlante.entity);
  const loading = useAppSelector(state => state.typePlante.loading);
  const updating = useAppSelector(state => state.typePlante.updating);
  const updateSuccess = useAppSelector(state => state.typePlante.updateSuccess);

  const handleClose = () => {
    navigate('/type-plante');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
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
    if (values.humiditeMax !== undefined && typeof values.humiditeMax !== 'number') {
      values.humiditeMax = Number(values.humiditeMax);
    }
    if (values.humiditeMin !== undefined && typeof values.humiditeMin !== 'number') {
      values.humiditeMin = Number(values.humiditeMin);
    }
    if (values.temperature !== undefined && typeof values.temperature !== 'number') {
      values.temperature = Number(values.temperature);
    }

    const entity = {
      ...typePlanteEntity,
      ...values,
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
          ...typePlanteEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="pharmaGesApp.typePlante.home.createOrEditLabel" data-cy="TypePlanteCreateUpdateHeading">
            <Translate contentKey="pharmaGesApp.typePlante.home.createOrEditLabel">Create or edit a TypePlante</Translate>
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
                  id="type-plante-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('pharmaGesApp.typePlante.name')}
                id="type-plante-name"
                name="name"
                data-cy="name"
                type="text"
              />
              <ValidatedField
                label={translate('pharmaGesApp.typePlante.humiditeMax')}
                id="type-plante-humiditeMax"
                name="humiditeMax"
                data-cy="humiditeMax"
                type="text"
              />
              <ValidatedField
                label={translate('pharmaGesApp.typePlante.humiditeMin')}
                id="type-plante-humiditeMin"
                name="humiditeMin"
                data-cy="humiditeMin"
                type="text"
              />
              <ValidatedField
                label={translate('pharmaGesApp.typePlante.temperature')}
                id="type-plante-temperature"
                name="temperature"
                data-cy="temperature"
                type="text"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/type-plante" replace color="info">
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

export default TypePlanteUpdate;
