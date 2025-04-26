import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getUsers } from 'app/shared/reducers/user-management';
import { getEntities as getSpecializations } from 'app/entities/specialization/specialization.reducer';
import { createEntity, getEntity, reset, updateEntity } from './doctor-profile.reducer';

export const DoctorProfileUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const specializations = useAppSelector(state => state.specialization.entities);
  const doctorProfileEntity = useAppSelector(state => state.doctorProfile.entity);
  const loading = useAppSelector(state => state.doctorProfile.loading);
  const updating = useAppSelector(state => state.doctorProfile.updating);
  const updateSuccess = useAppSelector(state => state.doctorProfile.updateSuccess);

  const handleClose = () => {
    navigate(`/doctor-profile${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
    dispatch(getSpecializations({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.latitude !== undefined && typeof values.latitude !== 'number') {
      values.latitude = Number(values.latitude);
    }
    if (values.longitude !== undefined && typeof values.longitude !== 'number') {
      values.longitude = Number(values.longitude);
    }
    values.lastLoginDate = convertDateTimeToServer(values.lastLoginDate);

    const entity = {
      ...doctorProfileEntity,
      ...values,
      internalUser: users.find(it => it.id.toString() === values.internalUser?.toString()),
      specializations: mapIdList(values.specializations),
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
          lastLoginDate: displayDefaultDateTime(),
        }
      : {
          ...doctorProfileEntity,
          lastLoginDate: convertDateTimeFromServer(doctorProfileEntity.lastLoginDate),
          internalUser: doctorProfileEntity?.internalUser?.id,
          specializations: doctorProfileEntity?.specializations?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="allomedApp.doctorProfile.home.createOrEditLabel" data-cy="DoctorProfileCreateUpdateHeading">
            <Translate contentKey="allomedApp.doctorProfile.home.createOrEditLabel">Create or edit a DoctorProfile</Translate>
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
                  id="doctor-profile-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('allomedApp.doctorProfile.phoneNumber')}
                id="doctor-profile-phoneNumber"
                name="phoneNumber"
                data-cy="phoneNumber"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('allomedApp.doctorProfile.officeAddress')}
                id="doctor-profile-officeAddress"
                name="officeAddress"
                data-cy="officeAddress"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('allomedApp.doctorProfile.latitude')}
                id="doctor-profile-latitude"
                name="latitude"
                data-cy="latitude"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('allomedApp.doctorProfile.longitude')}
                id="doctor-profile-longitude"
                name="longitude"
                data-cy="longitude"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('allomedApp.doctorProfile.inpeCode')}
                id="doctor-profile-inpeCode"
                name="inpeCode"
                data-cy="inpeCode"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('allomedApp.doctorProfile.isVerified')}
                id="doctor-profile-isVerified"
                name="isVerified"
                data-cy="isVerified"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('allomedApp.doctorProfile.lastLoginIp')}
                id="doctor-profile-lastLoginIp"
                name="lastLoginIp"
                data-cy="lastLoginIp"
                type="text"
              />
              <ValidatedField
                label={translate('allomedApp.doctorProfile.lastUserAgent')}
                id="doctor-profile-lastUserAgent"
                name="lastUserAgent"
                data-cy="lastUserAgent"
                type="text"
              />
              <ValidatedField
                label={translate('allomedApp.doctorProfile.lastLoginDate')}
                id="doctor-profile-lastLoginDate"
                name="lastLoginDate"
                data-cy="lastLoginDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="doctor-profile-internalUser"
                name="internalUser"
                data-cy="internalUser"
                label={translate('allomedApp.doctorProfile.internalUser')}
                type="select"
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                label={translate('allomedApp.doctorProfile.specializations')}
                id="doctor-profile-specializations"
                data-cy="specializations"
                type="select"
                multiple
                name="specializations"
              >
                <option value="" key="0" />
                {specializations
                  ? specializations.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/doctor-profile" replace color="info">
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

export default DoctorProfileUpdate;
