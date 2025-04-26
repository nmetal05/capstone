import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getSymptomSearches } from 'app/entities/symptom-search/symptom-search.reducer';
import { getEntities as getSpecializations } from 'app/entities/specialization/specialization.reducer';
import { createEntity, getEntity, reset, updateEntity } from './symptom-search-recommendation.reducer';

export const SymptomSearchRecommendationUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const symptomSearches = useAppSelector(state => state.symptomSearch.entities);
  const specializations = useAppSelector(state => state.specialization.entities);
  const symptomSearchRecommendationEntity = useAppSelector(state => state.symptomSearchRecommendation.entity);
  const loading = useAppSelector(state => state.symptomSearchRecommendation.loading);
  const updating = useAppSelector(state => state.symptomSearchRecommendation.updating);
  const updateSuccess = useAppSelector(state => state.symptomSearchRecommendation.updateSuccess);

  const handleClose = () => {
    navigate(`/symptom-search-recommendation${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getSymptomSearches({}));
    dispatch(getSpecializations({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.confidenceScore !== undefined && typeof values.confidenceScore !== 'number') {
      values.confidenceScore = Number(values.confidenceScore);
    }
    if (values.rank !== undefined && typeof values.rank !== 'number') {
      values.rank = Number(values.rank);
    }

    const entity = {
      ...symptomSearchRecommendationEntity,
      ...values,
      search: symptomSearches.find(it => it.id.toString() === values.search?.toString()),
      specialization: specializations.find(it => it.id.toString() === values.specialization?.toString()),
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
          ...symptomSearchRecommendationEntity,
          search: symptomSearchRecommendationEntity?.search?.id,
          specialization: symptomSearchRecommendationEntity?.specialization?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="allomedApp.symptomSearchRecommendation.home.createOrEditLabel" data-cy="SymptomSearchRecommendationCreateUpdateHeading">
            <Translate contentKey="allomedApp.symptomSearchRecommendation.home.createOrEditLabel">
              Create or edit a SymptomSearchRecommendation
            </Translate>
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
                  id="symptom-search-recommendation-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('allomedApp.symptomSearchRecommendation.confidenceScore')}
                id="symptom-search-recommendation-confidenceScore"
                name="confidenceScore"
                data-cy="confidenceScore"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('allomedApp.symptomSearchRecommendation.rank')}
                id="symptom-search-recommendation-rank"
                name="rank"
                data-cy="rank"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('allomedApp.symptomSearchRecommendation.reasoning')}
                id="symptom-search-recommendation-reasoning"
                name="reasoning"
                data-cy="reasoning"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="symptom-search-recommendation-search"
                name="search"
                data-cy="search"
                label={translate('allomedApp.symptomSearchRecommendation.search')}
                type="select"
                required
              >
                <option value="" key="0" />
                {symptomSearches
                  ? symptomSearches.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="symptom-search-recommendation-specialization"
                name="specialization"
                data-cy="specialization"
                label={translate('allomedApp.symptomSearchRecommendation.specialization')}
                type="select"
                required
              >
                <option value="" key="0" />
                {specializations
                  ? specializations.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button
                tag={Link}
                id="cancel-save"
                data-cy="entityCreateCancelButton"
                to="/symptom-search-recommendation"
                replace
                color="info"
              >
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

export default SymptomSearchRecommendationUpdate;
