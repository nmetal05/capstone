import React, { useState } from 'react';
import { CardHeader, CardBody, Form, FormGroup, Label, Input, Button, Alert } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Translate } from 'react-jhipster';
import axios from 'axios';

interface AppUserProfileProps {
  profile: any;
}

const AppUserProfile: React.FC<AppUserProfileProps> = ({ profile }) => {
  const [formData, setFormData] = useState({
    phoneNumber: profile.phoneNumber || '',
    address: profile.address || '',
    dateOfBirth: profile.dateOfBirth || '',
    gender: profile.gender || '',
    bloodType: profile.bloodType || '',
    emergencyContact: profile.emergencyContact || '',
    medicalHistory: profile.medicalHistory || '',
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
      await axios.put(`/api/app-user-profiles/${profile.id}`, formData);
      setSuccess(true);
    } catch (err) {
      setError('account.manage.appUserProfile.error');
      console.error('Error updating app user profile:', err);
    }
  };

  return (
    <>
      <CardHeader>
        <h3>
          <FontAwesomeIcon icon="user" />
          <Translate contentKey="account.manage.appUserProfile.title">App User Profile</Translate>
        </h3>
      </CardHeader>
      <CardBody>
        {success && (
          <Alert color="success">
            <Translate contentKey="account.manage.appUserProfile.success">Profile updated successfully</Translate>
          </Alert>
        )}
        {error && (
          <Alert color="danger">
            <Translate contentKey={error}>Error updating app user profile</Translate>
          </Alert>
        )}

        <Form onSubmit={handleSubmit}>
          <FormGroup>
            <Label for="phoneNumber">
              <Translate contentKey="account.manage.appUserProfile.phoneNumber">Phone Number</Translate>
            </Label>
            <Input
              type="tel"
              name="phoneNumber"
              id="phoneNumber"
              value={formData.phoneNumber}
              onChange={handleChange}
              placeholder="account.manage.appUserProfile.phoneNumber.placeholder"
            />
          </FormGroup>

          <FormGroup>
            <Label for="address">
              <Translate contentKey="account.manage.appUserProfile.address">Address</Translate>
            </Label>
            <Input
              type="text"
              name="address"
              id="address"
              value={formData.address}
              onChange={handleChange}
              placeholder="account.manage.appUserProfile.address.placeholder"
            />
          </FormGroup>

          <FormGroup>
            <Label for="dateOfBirth">
              <Translate contentKey="account.manage.appUserProfile.dateOfBirth">Date of Birth</Translate>
            </Label>
            <Input type="date" name="dateOfBirth" id="dateOfBirth" value={formData.dateOfBirth} onChange={handleChange} />
          </FormGroup>

          <FormGroup>
            <Label for="gender">
              <Translate contentKey="account.manage.appUserProfile.gender">Gender</Translate>
            </Label>
            <Input type="select" name="gender" id="gender" value={formData.gender} onChange={handleChange}>
              <option value="">
                <Translate contentKey="account.manage.appUserProfile.gender.select">Select gender</Translate>
              </option>
              <option value="MALE">
                <Translate contentKey="account.manage.appUserProfile.gender.male">Male</Translate>
              </option>
              <option value="FEMALE">
                <Translate contentKey="account.manage.appUserProfile.gender.female">Female</Translate>
              </option>
              <option value="OTHER">
                <Translate contentKey="account.manage.appUserProfile.gender.other">Other</Translate>
              </option>
            </Input>
          </FormGroup>

          <FormGroup>
            <Label for="bloodType">
              <Translate contentKey="account.manage.appUserProfile.bloodType">Blood Type</Translate>
            </Label>
            <Input type="select" name="bloodType" id="bloodType" value={formData.bloodType} onChange={handleChange}>
              <option value="">
                <Translate contentKey="account.manage.appUserProfile.bloodType.select">Select blood type</Translate>
              </option>
              <option value="A+">A+</option>
              <option value="A-">A-</option>
              <option value="B+">B+</option>
              <option value="B-">B-</option>
              <option value="AB+">AB+</option>
              <option value="AB-">AB-</option>
              <option value="O+">O+</option>
              <option value="O-">O-</option>
            </Input>
          </FormGroup>

          <FormGroup>
            <Label for="emergencyContact">
              <Translate contentKey="account.manage.appUserProfile.emergencyContact">Emergency Contact</Translate>
            </Label>
            <Input
              type="text"
              name="emergencyContact"
              id="emergencyContact"
              value={formData.emergencyContact}
              onChange={handleChange}
              placeholder="account.manage.appUserProfile.emergencyContact.placeholder"
            />
          </FormGroup>

          <FormGroup>
            <Label for="medicalHistory">
              <Translate contentKey="account.manage.appUserProfile.medicalHistory">Medical History</Translate>
            </Label>
            <Input
              type="textarea"
              name="medicalHistory"
              id="medicalHistory"
              value={formData.medicalHistory}
              onChange={handleChange}
              placeholder="account.manage.appUserProfile.medicalHistory.placeholder"
              rows={4}
            />
          </FormGroup>

          <Button type="submit" color="primary">
            <FontAwesomeIcon icon="save" className="me-2" />
            <Translate contentKey="account.manage.appUserProfile.save">Save Changes</Translate>
          </Button>
        </Form>
      </CardBody>
    </>
  );
};

export default AppUserProfile;
