import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Col, Form, FormGroup, Input, InputGroup, Row, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities, searchEntities } from './doctor-profile.reducer';

export const DoctorProfile = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const doctorProfileList = useAppSelector(state => state.doctorProfile.entities);
  const loading = useAppSelector(state => state.doctorProfile.loading);
  const totalItems = useAppSelector(state => state.doctorProfile.totalItems);

  const getAllEntities = () => {
    if (search) {
      dispatch(
        searchEntities({
          query: search,
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        }),
      );
    } else {
      dispatch(
        getEntities({
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        }),
      );
    }
  };

  const startSearching = e => {
    if (search) {
      setPaginationState({
        ...paginationState,
        activePage: 1,
      });
      dispatch(
        searchEntities({
          query: search,
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        }),
      );
    }
    e.preventDefault();
  };

  const clear = () => {
    setSearch('');
    setPaginationState({
      ...paginationState,
      activePage: 1,
    });
    dispatch(getEntities({}));
  };

  const handleSearch = event => setSearch(event.target.value);

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort, search]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="doctor-profile-heading" data-cy="DoctorProfileHeading">
        <Translate contentKey="allomedApp.doctorProfile.home.title">Doctor Profiles</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="allomedApp.doctorProfile.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/doctor-profile/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="allomedApp.doctorProfile.home.createLabel">Create new Doctor Profile</Translate>
          </Link>
        </div>
      </h2>
      <Row>
        <Col sm="12">
          <Form onSubmit={startSearching}>
            <FormGroup>
              <InputGroup>
                <Input
                  type="text"
                  name="search"
                  defaultValue={search}
                  onChange={handleSearch}
                  placeholder={translate('allomedApp.doctorProfile.home.search')}
                />
                <Button className="input-group-addon">
                  <FontAwesomeIcon icon="search" />
                </Button>
                <Button type="reset" className="input-group-addon" onClick={clear}>
                  <FontAwesomeIcon icon="trash" />
                </Button>
              </InputGroup>
            </FormGroup>
          </Form>
        </Col>
      </Row>
      <div className="table-responsive">
        {doctorProfileList && doctorProfileList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="allomedApp.doctorProfile.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('phoneNumber')}>
                  <Translate contentKey="allomedApp.doctorProfile.phoneNumber">Phone Number</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('phoneNumber')} />
                </th>
                <th className="hand" onClick={sort('officeAddress')}>
                  <Translate contentKey="allomedApp.doctorProfile.officeAddress">Office Address</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('officeAddress')} />
                </th>
                <th className="hand" onClick={sort('latitude')}>
                  <Translate contentKey="allomedApp.doctorProfile.latitude">Latitude</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('latitude')} />
                </th>
                <th className="hand" onClick={sort('longitude')}>
                  <Translate contentKey="allomedApp.doctorProfile.longitude">Longitude</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('longitude')} />
                </th>
                <th className="hand" onClick={sort('inpeCode')}>
                  <Translate contentKey="allomedApp.doctorProfile.inpeCode">Inpe Code</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('inpeCode')} />
                </th>
                <th className="hand" onClick={sort('isVerified')}>
                  <Translate contentKey="allomedApp.doctorProfile.isVerified">Is Verified</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isVerified')} />
                </th>
                <th className="hand" onClick={sort('lastLoginIp')}>
                  <Translate contentKey="allomedApp.doctorProfile.lastLoginIp">Last Login Ip</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('lastLoginIp')} />
                </th>
                <th className="hand" onClick={sort('lastUserAgent')}>
                  <Translate contentKey="allomedApp.doctorProfile.lastUserAgent">Last User Agent</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('lastUserAgent')} />
                </th>
                <th className="hand" onClick={sort('lastLoginDate')}>
                  <Translate contentKey="allomedApp.doctorProfile.lastLoginDate">Last Login Date</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('lastLoginDate')} />
                </th>
                <th>
                  <Translate contentKey="allomedApp.doctorProfile.internalUser">Internal User</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {doctorProfileList.map((doctorProfile, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/doctor-profile/${doctorProfile.id}`} color="link" size="sm">
                      {doctorProfile.id}
                    </Button>
                  </td>
                  <td>{doctorProfile.phoneNumber}</td>
                  <td>{doctorProfile.officeAddress}</td>
                  <td>{doctorProfile.latitude}</td>
                  <td>{doctorProfile.longitude}</td>
                  <td>{doctorProfile.inpeCode}</td>
                  <td>{doctorProfile.isVerified ? 'true' : 'false'}</td>
                  <td>{doctorProfile.lastLoginIp}</td>
                  <td>{doctorProfile.lastUserAgent}</td>
                  <td>
                    {doctorProfile.lastLoginDate ? (
                      <TextFormat type="date" value={doctorProfile.lastLoginDate} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{doctorProfile.internalUser ? doctorProfile.internalUser.login : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/doctor-profile/${doctorProfile.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/doctor-profile/${doctorProfile.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() =>
                          (window.location.href = `/doctor-profile/${doctorProfile.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="allomedApp.doctorProfile.home.notFound">No Doctor Profiles found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={doctorProfileList && doctorProfileList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default DoctorProfile;
