import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './doctor-view-history.reducer';

export const DoctorViewHistoryDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const doctorViewHistoryEntity = useAppSelector(state => state.doctorViewHistory.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="doctorViewHistoryDetailsHeading">
          <Translate contentKey="allomedApp.doctorViewHistory.detail.title">DoctorViewHistory</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{doctorViewHistoryEntity.id}</dd>
          <dt>
            <span id="viewDate">
              <Translate contentKey="allomedApp.doctorViewHistory.viewDate">View Date</Translate>
            </span>
          </dt>
          <dd>
            {doctorViewHistoryEntity.viewDate ? (
              <TextFormat value={doctorViewHistoryEntity.viewDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="allomedApp.doctorViewHistory.user">User</Translate>
          </dt>
          <dd>{doctorViewHistoryEntity.user ? doctorViewHistoryEntity.user.id : ''}</dd>
          <dt>
            <Translate contentKey="allomedApp.doctorViewHistory.doctor">Doctor</Translate>
          </dt>
          <dd>{doctorViewHistoryEntity.doctor ? doctorViewHistoryEntity.doctor.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/doctor-view-history" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/doctor-view-history/${doctorViewHistoryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DoctorViewHistoryDetail;
