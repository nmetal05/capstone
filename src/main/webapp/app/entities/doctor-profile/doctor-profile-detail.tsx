import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './doctor-profile.reducer';

export const DoctorProfileDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const doctorProfileEntity = useAppSelector(state => state.doctorProfile.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="doctorProfileDetailsHeading">
          <Translate contentKey="allomedApp.doctorProfile.detail.title">DoctorProfile</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{doctorProfileEntity.id}</dd>
          <dt>
            <span id="phoneNumber">
              <Translate contentKey="allomedApp.doctorProfile.phoneNumber">Phone Number</Translate>
            </span>
          </dt>
          <dd>{doctorProfileEntity.phoneNumber}</dd>
          <dt>
            <span id="officeAddress">
              <Translate contentKey="allomedApp.doctorProfile.officeAddress">Office Address</Translate>
            </span>
          </dt>
          <dd>{doctorProfileEntity.officeAddress}</dd>
          <dt>
            <span id="latitude">
              <Translate contentKey="allomedApp.doctorProfile.latitude">Latitude</Translate>
            </span>
          </dt>
          <dd>{doctorProfileEntity.latitude}</dd>
          <dt>
            <span id="longitude">
              <Translate contentKey="allomedApp.doctorProfile.longitude">Longitude</Translate>
            </span>
          </dt>
          <dd>{doctorProfileEntity.longitude}</dd>
          <dt>
            <span id="inpeCode">
              <Translate contentKey="allomedApp.doctorProfile.inpeCode">Inpe Code</Translate>
            </span>
          </dt>
          <dd>{doctorProfileEntity.inpeCode}</dd>
          <dt>
            <span id="isVerified">
              <Translate contentKey="allomedApp.doctorProfile.isVerified">Is Verified</Translate>
            </span>
          </dt>
          <dd>{doctorProfileEntity.isVerified ? 'true' : 'false'}</dd>
          <dt>
            <span id="lastLoginIp">
              <Translate contentKey="allomedApp.doctorProfile.lastLoginIp">Last Login Ip</Translate>
            </span>
          </dt>
          <dd>{doctorProfileEntity.lastLoginIp}</dd>
          <dt>
            <span id="lastUserAgent">
              <Translate contentKey="allomedApp.doctorProfile.lastUserAgent">Last User Agent</Translate>
            </span>
          </dt>
          <dd>{doctorProfileEntity.lastUserAgent}</dd>
          <dt>
            <span id="lastLoginDate">
              <Translate contentKey="allomedApp.doctorProfile.lastLoginDate">Last Login Date</Translate>
            </span>
          </dt>
          <dd>
            {doctorProfileEntity.lastLoginDate ? (
              <TextFormat value={doctorProfileEntity.lastLoginDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="allomedApp.doctorProfile.internalUser">Internal User</Translate>
          </dt>
          <dd>{doctorProfileEntity.internalUser ? doctorProfileEntity.internalUser.login : ''}</dd>
          <dt>
            <Translate contentKey="allomedApp.doctorProfile.specializations">Specializations</Translate>
          </dt>
          <dd>
            {doctorProfileEntity.specializations
              ? doctorProfileEntity.specializations.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.name}</a>
                    {doctorProfileEntity.specializations && i === doctorProfileEntity.specializations.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/doctor-profile" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/doctor-profile/${doctorProfileEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DoctorProfileDetail;
