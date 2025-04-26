import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedBlobField, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getDoctorProfiles } from 'app/entities/doctor-profile/doctor-profile.reducer';
import { DocumentType } from 'app/shared/model/enumerations/document-type.model';
import { VerificationStatus } from 'app/shared/model/enumerations/verification-status.model';
import { createEntity, getEntity, reset, updateEntity } from './doctor-document.reducer';

export const DoctorDocumentUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const doctorProfiles = useAppSelector(state => state.doctorProfile.entities);
  const doctorDocumentEntity = useAppSelector(state => state.doctorDocument.entity);
  const loading = useAppSelector(state => state.doctorDocument.loading);
  const updating = useAppSelector(state => state.doctorDocument.updating);
  const updateSuccess = useAppSelector(state => state.doctorDocument.updateSuccess);
  const documentTypeValues = Object.keys(DocumentType);
  const verificationStatusValues = Object.keys(VerificationStatus);

  const handleClose = () => {
    navigate(`/doctor-document${location.search}`);
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
    values.uploadDate = convertDateTimeToServer(values.uploadDate);

    const entity = {
      ...doctorDocumentEntity,
      ...values,
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
          uploadDate: displayDefaultDateTime(),
        }
      : {
          type: 'DIPLOMA',
          verificationStatus: 'PENDING',
          ...doctorDocumentEntity,
          uploadDate: convertDateTimeFromServer(doctorDocumentEntity.uploadDate),
          doctor: doctorDocumentEntity?.doctor?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="allomedApp.doctorDocument.home.createOrEditLabel" data-cy="DoctorDocumentCreateUpdateHeading">
            <Translate contentKey="allomedApp.doctorDocument.home.createOrEditLabel">Create or edit a DoctorDocument</Translate>
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
                  id="doctor-document-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('allomedApp.doctorDocument.type')}
                id="doctor-document-type"
                name="type"
                data-cy="type"
                type="select"
              >
                {documentTypeValues.map(documentType => (
                  <option value={documentType} key={documentType}>
                    {translate(`allomedApp.DocumentType.${documentType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('allomedApp.doctorDocument.fileName')}
                id="doctor-document-fileName"
                name="fileName"
                data-cy="fileName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedBlobField
                label={translate('allomedApp.doctorDocument.fileContent')}
                id="doctor-document-fileContent"
                name="fileContent"
                data-cy="fileContent"
                openActionLabel={translate('entity.action.open')}
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('allomedApp.doctorDocument.uploadDate')}
                id="doctor-document-uploadDate"
                name="uploadDate"
                data-cy="uploadDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('allomedApp.doctorDocument.verificationStatus')}
                id="doctor-document-verificationStatus"
                name="verificationStatus"
                data-cy="verificationStatus"
                type="select"
              >
                {verificationStatusValues.map(verificationStatus => (
                  <option value={verificationStatus} key={verificationStatus}>
                    {translate(`allomedApp.VerificationStatus.${verificationStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                id="doctor-document-doctor"
                name="doctor"
                data-cy="doctor"
                label={translate('allomedApp.doctorDocument.doctor')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/doctor-document" replace color="info">
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

export default DoctorDocumentUpdate;
