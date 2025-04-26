import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getDoctorProfiles } from 'app/entities/doctor-profile/doctor-profile.reducer';
import { createEntity, getEntity, reset, updateEntity } from './specialization.reducer';

export const SpecializationUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const doctorProfiles = useAppSelector(state => state.doctorProfile.entities);
  const specializationEntity = useAppSelector(state => state.specialization.entity);
  const loading = useAppSelector(state => state.specialization.loading);
  const updating = useAppSelector(state => state.specialization.updating);
  const updateSuccess = useAppSelector(state => state.specialization.updateSuccess);

  const handleClose = () => {
    navigate(`/specialization${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

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

    const entity = {
      ...specializationEntity,
      ...values,
      doctorProfiles: mapIdList(values.doctorProfiles),
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
          ...specializationEntity,
          doctorProfiles: specializationEntity?.doctorProfiles?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="allomedApp.specialization.home.createOrEditLabel" data-cy="SpecializationCreateUpdateHeading">
            <Translate contentKey="allomedApp.specialization.home.createOrEditLabel">Create or edit a Specialization</Translate>
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
                  id="specialization-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('allomedApp.specialization.name')}
                id="specialization-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('allomedApp.specialization.description')}
                id="specialization-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <ValidatedField
                label={translate('allomedApp.specialization.doctorProfiles')}
                id="specialization-doctorProfiles"
                data-cy="doctorProfiles"
                type="select"
                multiple
                name="doctorProfiles"
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/specialization" replace color="info">
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

export default SpecializationUpdate;
