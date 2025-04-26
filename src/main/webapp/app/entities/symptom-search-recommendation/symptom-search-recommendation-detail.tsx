import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './symptom-search-recommendation.reducer';

export const SymptomSearchRecommendationDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const symptomSearchRecommendationEntity = useAppSelector(state => state.symptomSearchRecommendation.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="symptomSearchRecommendationDetailsHeading">
          <Translate contentKey="allomedApp.symptomSearchRecommendation.detail.title">SymptomSearchRecommendation</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{symptomSearchRecommendationEntity.id}</dd>
          <dt>
            <span id="confidenceScore">
              <Translate contentKey="allomedApp.symptomSearchRecommendation.confidenceScore">Confidence Score</Translate>
            </span>
          </dt>
          <dd>{symptomSearchRecommendationEntity.confidenceScore}</dd>
          <dt>
            <span id="rank">
              <Translate contentKey="allomedApp.symptomSearchRecommendation.rank">Rank</Translate>
            </span>
          </dt>
          <dd>{symptomSearchRecommendationEntity.rank}</dd>
          <dt>
            <span id="reasoning">
              <Translate contentKey="allomedApp.symptomSearchRecommendation.reasoning">Reasoning</Translate>
            </span>
          </dt>
          <dd>{symptomSearchRecommendationEntity.reasoning}</dd>
          <dt>
            <Translate contentKey="allomedApp.symptomSearchRecommendation.search">Search</Translate>
          </dt>
          <dd>{symptomSearchRecommendationEntity.search ? symptomSearchRecommendationEntity.search.id : ''}</dd>
          <dt>
            <Translate contentKey="allomedApp.symptomSearchRecommendation.specialization">Specialization</Translate>
          </dt>
          <dd>{symptomSearchRecommendationEntity.specialization ? symptomSearchRecommendationEntity.specialization.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/symptom-search-recommendation" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/symptom-search-recommendation/${symptomSearchRecommendationEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default SymptomSearchRecommendationDetail;
