import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getUsers } from 'app/shared/reducers/user-management';
import { createEntity, getEntity, reset, updateEntity } from './app-user-profile.reducer';

export const AppUserProfileUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const appUserProfileEntity = useAppSelector(state => state.appUserProfile.entity);
  const loading = useAppSelector(state => state.appUserProfile.loading);
  const updating = useAppSelector(state => state.appUserProfile.updating);
  const updateSuccess = useAppSelector(state => state.appUserProfile.updateSuccess);

  const handleClose = () => {
    navigate(`/app-user-profile${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
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
      ...appUserProfileEntity,
      ...values,
      internalUser: users.find(it => it.id.toString() === values.internalUser?.toString()),
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
          ...appUserProfileEntity,
          lastLoginDate: convertDateTimeFromServer(appUserProfileEntity.lastLoginDate),
          internalUser: appUserProfileEntity?.internalUser?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="allomedApp.appUserProfile.home.createOrEditLabel" data-cy="AppUserProfileCreateUpdateHeading">
            <Translate contentKey="allomedApp.appUserProfile.home.createOrEditLabel">Create or edit a AppUserProfile</Translate>
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
                  id="app-user-profile-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('allomedApp.appUserProfile.latitude')}
                id="app-user-profile-latitude"
                name="latitude"
                data-cy="latitude"
                type="text"
              />
              <ValidatedField
                label={translate('allomedApp.appUserProfile.longitude')}
                id="app-user-profile-longitude"
                name="longitude"
                data-cy="longitude"
                type="text"
              />
              <ValidatedField
                label={translate('allomedApp.appUserProfile.lastLoginIp')}
                id="app-user-profile-lastLoginIp"
                name="lastLoginIp"
                data-cy="lastLoginIp"
                type="text"
              />
              <ValidatedField
                label={translate('allomedApp.appUserProfile.lastUserAgent')}
                id="app-user-profile-lastUserAgent"
                name="lastUserAgent"
                data-cy="lastUserAgent"
                type="text"
              />
              <ValidatedField
                label={translate('allomedApp.appUserProfile.lastLoginDate')}
                id="app-user-profile-lastLoginDate"
                name="lastLoginDate"
                data-cy="lastLoginDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="app-user-profile-internalUser"
                name="internalUser"
                data-cy="internalUser"
                label={translate('allomedApp.appUserProfile.internalUser')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/app-user-profile" replace color="info">
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

export default AppUserProfileUpdate;
