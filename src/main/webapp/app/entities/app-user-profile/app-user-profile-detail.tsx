import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './app-user-profile.reducer';

export const AppUserProfileDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const appUserProfileEntity = useAppSelector(state => state.appUserProfile.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="appUserProfileDetailsHeading">
          <Translate contentKey="allomedApp.appUserProfile.detail.title">AppUserProfile</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{appUserProfileEntity.id}</dd>
          <dt>
            <span id="latitude">
              <Translate contentKey="allomedApp.appUserProfile.latitude">Latitude</Translate>
            </span>
          </dt>
          <dd>{appUserProfileEntity.latitude}</dd>
          <dt>
            <span id="longitude">
              <Translate contentKey="allomedApp.appUserProfile.longitude">Longitude</Translate>
            </span>
          </dt>
          <dd>{appUserProfileEntity.longitude}</dd>
          <dt>
            <span id="lastLoginIp">
              <Translate contentKey="allomedApp.appUserProfile.lastLoginIp">Last Login Ip</Translate>
            </span>
          </dt>
          <dd>{appUserProfileEntity.lastLoginIp}</dd>
          <dt>
            <span id="lastUserAgent">
              <Translate contentKey="allomedApp.appUserProfile.lastUserAgent">Last User Agent</Translate>
            </span>
          </dt>
          <dd>{appUserProfileEntity.lastUserAgent}</dd>
          <dt>
            <span id="lastLoginDate">
              <Translate contentKey="allomedApp.appUserProfile.lastLoginDate">Last Login Date</Translate>
            </span>
          </dt>
          <dd>
            {appUserProfileEntity.lastLoginDate ? (
              <TextFormat value={appUserProfileEntity.lastLoginDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="allomedApp.appUserProfile.internalUser">Internal User</Translate>
          </dt>
          <dd>{appUserProfileEntity.internalUser ? appUserProfileEntity.internalUser.login : ''}</dd>
        </dl>
        <Button tag={Link} to="/app-user-profile" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/app-user-profile/${appUserProfileEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AppUserProfileDetail;
