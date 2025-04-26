import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getAppUserProfiles } from 'app/entities/app-user-profile/app-user-profile.reducer';
import { getEntities as getDoctorProfiles } from 'app/entities/doctor-profile/doctor-profile.reducer';
import { createEntity, getEntity, reset, updateEntity } from './doctor-view-history.reducer';

export const DoctorViewHistoryUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const appUserProfiles = useAppSelector(state => state.appUserProfile.entities);
  const doctorProfiles = useAppSelector(state => state.doctorProfile.entities);
  const doctorViewHistoryEntity = useAppSelector(state => state.doctorViewHistory.entity);
  const loading = useAppSelector(state => state.doctorViewHistory.loading);
  const updating = useAppSelector(state => state.doctorViewHistory.updating);
  const updateSuccess = useAppSelector(state => state.doctorViewHistory.updateSuccess);

  const handleClose = () => {
    navigate(`/doctor-view-history${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getAppUserProfiles({}));
    dispatch(getDoctorProfiles({}));
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
    values.viewDate = convertDateTimeToServer(values.viewDate);

    const entity = {
      ...doctorViewHistoryEntity,
      ...values,
      user: appUserProfiles.find(it => it.id.toString() === values.user?.toString()),
      doctor: doctorProfiles.find(it => it.id.toString() === values.doctor?.toString()),
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
          viewDate: displayDefaultDateTime(),
        }
      : {
          ...doctorViewHistoryEntity,
          viewDate: convertDateTimeFromServer(doctorViewHistoryEntity.viewDate),
          user: doctorViewHistoryEntity?.user?.id,
          doctor: doctorViewHistoryEntity?.doctor?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="allomedApp.doctorViewHistory.home.createOrEditLabel" data-cy="DoctorViewHistoryCreateUpdateHeading">
            <Translate contentKey="allomedApp.doctorViewHistory.home.createOrEditLabel">Create or edit a DoctorViewHistory</Translate>
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
                  id="doctor-view-history-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('allomedApp.doctorViewHistory.viewDate')}
                id="doctor-view-history-viewDate"
                name="viewDate"
                data-cy="viewDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="doctor-view-history-user"
                name="user"
                data-cy="user"
                label={translate('allomedApp.doctorViewHistory.user')}
                type="select"
                required
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
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="doctor-view-history-doctor"
                name="doctor"
                data-cy="doctor"
                label={translate('allomedApp.doctorViewHistory.doctor')}
                type="select"
                required
              >
                <option value="" key="0" />
                {doctorProfiles
                  ? doctorProfiles.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/doctor-view-history" replace color="info">
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

export default DoctorViewHistoryUpdate;
