import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './specialization.reducer';

export const SpecializationDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const specializationEntity = useAppSelector(state => state.specialization.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="specializationDetailsHeading">
          <Translate contentKey="allomedApp.specialization.detail.title">Specialization</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{specializationEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="allomedApp.specialization.name">Name</Translate>
            </span>
          </dt>
          <dd>{specializationEntity.name}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="allomedApp.specialization.description">Description</Translate>
            </span>
          </dt>
          <dd>{specializationEntity.description}</dd>
          <dt>
            <Translate contentKey="allomedApp.specialization.doctorProfiles">Doctor Profiles</Translate>
          </dt>
          <dd>
            {specializationEntity.doctorProfiles
              ? specializationEntity.doctorProfiles.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {specializationEntity.doctorProfiles && i === specializationEntity.doctorProfiles.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/specialization" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/specialization/${specializationEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default SpecializationDetail;
