import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './app-user.reducer';

export const AppUserDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const appUserEntity = useAppSelector(state => state.appUser.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="appUserDetailsHeading">
          <Translate contentKey="pharmaGesApp.appUser.detail.title">AppUser</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{appUserEntity.id}</dd>
          <dt>
            <span id="username">
              <Translate contentKey="pharmaGesApp.appUser.username">Username</Translate>
            </span>
          </dt>
          <dd>{appUserEntity.username}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="pharmaGesApp.appUser.email">Email</Translate>
            </span>
          </dt>
          <dd>{appUserEntity.email}</dd>
          <dt>
            <span id="password">
              <Translate contentKey="pharmaGesApp.appUser.password">Password</Translate>
            </span>
          </dt>
          <dd>{appUserEntity.password}</dd>
          <dt>
            <span id="address">
              <Translate contentKey="pharmaGesApp.appUser.address">Address</Translate>
            </span>
          </dt>
          <dd>{appUserEntity.address}</dd>
          <dt>
            <span id="phone">
              <Translate contentKey="pharmaGesApp.appUser.phone">Phone</Translate>
            </span>
          </dt>
          <dd>{appUserEntity.phone}</dd>
          <dt>
            <span id="role">
              <Translate contentKey="pharmaGesApp.appUser.role">Role</Translate>
            </span>
          </dt>
          <dd>{appUserEntity.role}</dd>
          <dt>
            <span id="image">
              <Translate contentKey="pharmaGesApp.appUser.image">Image</Translate>
            </span>
          </dt>
          <dd>{appUserEntity.image}</dd>
        </dl>
        <Button tag={Link} to="/app-user" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/app-user/${appUserEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AppUserDetail;
