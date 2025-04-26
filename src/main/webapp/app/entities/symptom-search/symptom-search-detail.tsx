import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './symptom-search.reducer';

export const SymptomSearchDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const symptomSearchEntity = useAppSelector(state => state.symptomSearch.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="symptomSearchDetailsHeading">
          <Translate contentKey="allomedApp.symptomSearch.detail.title">SymptomSearch</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{symptomSearchEntity.id}</dd>
          <dt>
            <span id="searchDate">
              <Translate contentKey="allomedApp.symptomSearch.searchDate">Search Date</Translate>
            </span>
          </dt>
          <dd>
            {symptomSearchEntity.searchDate ? (
              <TextFormat value={symptomSearchEntity.searchDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="symptoms">
              <Translate contentKey="allomedApp.symptomSearch.symptoms">Symptoms</Translate>
            </span>
          </dt>
          <dd>{symptomSearchEntity.symptoms}</dd>
          <dt>
            <span id="aiResponseJson">
              <Translate contentKey="allomedApp.symptomSearch.aiResponseJson">Ai Response Json</Translate>
            </span>
          </dt>
          <dd>{symptomSearchEntity.aiResponseJson}</dd>
          <dt>
            <Translate contentKey="allomedApp.symptomSearch.user">User</Translate>
          </dt>
          <dd>{symptomSearchEntity.user ? symptomSearchEntity.user.id : ''}</dd>
          <dt>
            <Translate contentKey="allomedApp.symptomSearch.guestSession">Guest Session</Translate>
          </dt>
          <dd>{symptomSearchEntity.guestSession ? symptomSearchEntity.guestSession.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/symptom-search" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/symptom-search/${symptomSearchEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default SymptomSearchDetail;
