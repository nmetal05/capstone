import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate, byteSize, openFile } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './doctor-document.reducer';

export const DoctorDocumentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const doctorDocumentEntity = useAppSelector(state => state.doctorDocument.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="doctorDocumentDetailsHeading">
          <Translate contentKey="allomedApp.doctorDocument.detail.title">DoctorDocument</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{doctorDocumentEntity.id}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="allomedApp.doctorDocument.type">Type</Translate>
            </span>
          </dt>
          <dd>{doctorDocumentEntity.type}</dd>
          <dt>
            <span id="fileName">
              <Translate contentKey="allomedApp.doctorDocument.fileName">File Name</Translate>
            </span>
          </dt>
          <dd>{doctorDocumentEntity.fileName}</dd>
          <dt>
            <span id="fileContent">
              <Translate contentKey="allomedApp.doctorDocument.fileContent">File Content</Translate>
            </span>
          </dt>
          <dd>
            {doctorDocumentEntity.fileContent ? (
              <div>
                {doctorDocumentEntity.fileContentContentType ? (
                  <a onClick={openFile(doctorDocumentEntity.fileContentContentType, doctorDocumentEntity.fileContent)}>
                    <Translate contentKey="entity.action.open">Open</Translate>&nbsp;
                  </a>
                ) : null}
                <span>
                  {doctorDocumentEntity.fileContentContentType}, {byteSize(doctorDocumentEntity.fileContent)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="uploadDate">
              <Translate contentKey="allomedApp.doctorDocument.uploadDate">Upload Date</Translate>
            </span>
          </dt>
          <dd>
            {doctorDocumentEntity.uploadDate ? (
              <TextFormat value={doctorDocumentEntity.uploadDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="verificationStatus">
              <Translate contentKey="allomedApp.doctorDocument.verificationStatus">Verification Status</Translate>
            </span>
          </dt>
          <dd>{doctorDocumentEntity.verificationStatus}</dd>
          <dt>
            <Translate contentKey="allomedApp.doctorDocument.doctor">Doctor</Translate>
          </dt>
          <dd>{doctorDocumentEntity.doctor ? doctorDocumentEntity.doctor.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/doctor-document" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/doctor-document/${doctorDocumentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DoctorDocumentDetail;
