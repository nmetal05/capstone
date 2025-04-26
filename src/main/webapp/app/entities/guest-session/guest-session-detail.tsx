import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './guest-session.reducer';

export const GuestSessionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const guestSessionEntity = useAppSelector(state => state.guestSession.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="guestSessionDetailsHeading">
          <Translate contentKey="allomedApp.guestSession.detail.title">GuestSession</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{guestSessionEntity.id}</dd>
          <dt>
            <span id="sessionId">
              <Translate contentKey="allomedApp.guestSession.sessionId">Session Id</Translate>
            </span>
          </dt>
          <dd>{guestSessionEntity.sessionId}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="allomedApp.guestSession.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {guestSessionEntity.createdAt ? <TextFormat value={guestSessionEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="lastActiveAt">
              <Translate contentKey="allomedApp.guestSession.lastActiveAt">Last Active At</Translate>
            </span>
          </dt>
          <dd>
            {guestSessionEntity.lastActiveAt ? (
              <TextFormat value={guestSessionEntity.lastActiveAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="ipAddress">
              <Translate contentKey="allomedApp.guestSession.ipAddress">Ip Address</Translate>
            </span>
          </dt>
          <dd>{guestSessionEntity.ipAddress}</dd>
          <dt>
            <span id="userAgent">
              <Translate contentKey="allomedApp.guestSession.userAgent">User Agent</Translate>
            </span>
          </dt>
          <dd>{guestSessionEntity.userAgent}</dd>
        </dl>
        <Button tag={Link} to="/guest-session" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/guest-session/${guestSessionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default GuestSessionDetail;
