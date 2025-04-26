import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getAppUserProfiles } from 'app/entities/app-user-profile/app-user-profile.reducer';
import { getEntities as getGuestSessions } from 'app/entities/guest-session/guest-session.reducer';
import { createEntity, getEntity, reset, updateEntity } from './symptom-search.reducer';

export const SymptomSearchUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const appUserProfiles = useAppSelector(state => state.appUserProfile.entities);
  const guestSessions = useAppSelector(state => state.guestSession.entities);
  const symptomSearchEntity = useAppSelector(state => state.symptomSearch.entity);
  const loading = useAppSelector(state => state.symptomSearch.loading);
  const updating = useAppSelector(state => state.symptomSearch.updating);
  const updateSuccess = useAppSelector(state => state.symptomSearch.updateSuccess);

  const handleClose = () => {
    navigate(`/symptom-search${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getAppUserProfiles({}));
    dispatch(getGuestSessions({}));
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
    values.searchDate = convertDateTimeToServer(values.searchDate);

    const entity = {
      ...symptomSearchEntity,
      ...values,
      user: appUserProfiles.find(it => it.id.toString() === values.user?.toString()),
      guestSession: guestSessions.find(it => it.id.toString() === values.guestSession?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          searchDate: displayDefaultDateTime(),
        }
      : {
          ...symptomSearchEntity,
          searchDate: convertDateTimeFromServer(symptomSearchEntity.searchDate),
          user: symptomSearchEntity?.user?.id,
          guestSession: symptomSearchEntity?.guestSession?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="allomedApp.symptomSearch.home.createOrEditLabel" data-cy="SymptomSearchCreateUpdateHeading">
            <Translate contentKey="allomedApp.symptomSearch.home.createOrEditLabel">Create or edit a SymptomSearch</Translate>
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
                  id="symptom-search-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('allomedApp.symptomSearch.searchDate')}
                id="symptom-search-searchDate"
                name="searchDate"
                data-cy="searchDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('allomedApp.symptomSearch.symptoms')}
                id="symptom-search-symptoms"
                name="symptoms"
                data-cy="symptoms"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('allomedApp.symptomSearch.aiResponseJson')}
                id="symptom-search-aiResponseJson"
                name="aiResponseJson"
                data-cy="aiResponseJson"
                type="textarea"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="symptom-search-user"
                name="user"
                data-cy="user"
                label={translate('allomedApp.symptomSearch.user')}
                type="select"
              >
                <option value="" key="0" />
                {appUserProfiles
                  ? appUserProfiles.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="symptom-search-guestSession"
                name="guestSession"
                data-cy="guestSession"
                label={translate('allomedApp.symptomSearch.guestSession')}
                type="select"
              >
                <option value="" key="0" />
                {guestSessions
                  ? guestSessions.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/symptom-search" replace color="info">
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

export default SymptomSearchUpdate;
