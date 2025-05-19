import React, { useState } from 'react';
import { CardHeader, CardBody, Form, FormGroup, Label, Input, Button, Alert } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Translate } from 'react-jhipster';
import axios from 'axios';

interface DoctorProfileProps {
  profile: any;
}

const DoctorProfile: React.FC<DoctorProfileProps> = ({ profile }) => {
  const [formData, setFormData] = useState({
    id: profile.id || '',
    phoneNumber: profile.phoneNumber || '',
    officeAddress: profile.officeAddress || '',
    inpeCode: profile.inpeCode || '',
    latitude: profile.latitude || 0,
    longitude: profile.longitude || 0,
    isVerified: profile.isVerified || false,
    internalUser: profile.internalUser || null,
    specializations: profile.specializations || [],
  });
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSuccess(false);
    setError('');

    try {
      await axios.put(`/api/doctor-profiles/${profile.id}`, formData);
      setSuccess(true);
    } catch (err) {
      setError('account.manage.doctorProfile.error');
      console.error('Error updating doctor profile:', err);
    }
  };

  return (
    <>
      <CardHeader>
        <h3>
          <FontAwesomeIcon icon="user-md" />
          <Translate contentKey="account.manage.doctorProfile.title">Doctor Profile</Translate>
        </h3>
      </CardHeader>
      <CardBody>
        {success && (
          <Alert color="success">
            <Translate contentKey="account.manage.doctorProfile.success">Profile updated successfully</Translate>
          </Alert>
        )}
        {error && (
          <Alert color="danger">
            <Translate contentKey={error}>Error updating doctor profile</Translate>
          </Alert>
        )}

        <Form onSubmit={handleSubmit}>
          <FormGroup>
            <Label for="phoneNumber">
              <Translate contentKey="account.manage.doctorProfile.phoneNumber">Phone Number</Translate>
            </Label>
            <Input
              type="tel"
              name="phoneNumber"
              id="phoneNumber"
              value={formData.phoneNumber}
              onChange={handleChange}
              placeholder="account.manage.doctorProfile.phoneNumber.placeholder"
              required
            />
          </FormGroup>

          <FormGroup>
            <Label for="officeAddress">
              <Translate contentKey="account.manage.doctorProfile.officeAddress">Office Address</Translate>
            </Label>
            <Input
              type="text"
              name="officeAddress"
              id="officeAddress"
              value={formData.officeAddress}
              onChange={handleChange}
              placeholder="account.manage.doctorProfile.officeAddress.placeholder"
              required
            />
          </FormGroup>

          <FormGroup>
            <Label for="inpeCode">
              <Translate contentKey="account.manage.doctorProfile.inpeCode">INPE Code</Translate>
            </Label>
            <Input
              type="text"
              name="inpeCode"
              id="inpeCode"
              value={formData.inpeCode}
              onChange={handleChange}
              placeholder="account.manage.doctorProfile.inpeCode.placeholder"
              required
            />
          </FormGroup>

          <input type="hidden" name="id" value={formData.id} />
          <input type="hidden" name="latitude" value={formData.latitude} />
          <input type="hidden" name="longitude" value={formData.longitude} />
          <input type="hidden" name="isVerified" value={formData.isVerified.toString()} />
          <input type="hidden" name="internalUser" value={JSON.stringify(formData.internalUser)} />
          <input type="hidden" name="specializations" value={JSON.stringify(formData.specializations)} />

          <Button type="submit" color="primary">
            <FontAwesomeIcon icon="save" className="me-2" />
            <Translate contentKey="account.manage.doctorProfile.save">Save Changes</Translate>
          </Button>
        </Form>
      </CardBody>
    </>
  );
};

export default DoctorProfile;
