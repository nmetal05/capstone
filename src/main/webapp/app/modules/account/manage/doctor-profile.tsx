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
    id: profile.id,
    phoneNumber: profile.phoneNumber || '',
    officeAddress: profile.officeAddress || '',
    specialization: profile.specialization || '',
    licenseNumber: profile.licenseNumber || '',
    yearsOfExperience: profile.yearsOfExperience || '',
    consultationFee: profile.consultationFee || '',
    workingHours: profile.workingHours || '',
    latitude: profile.latitude || 0,
    longitude: profile.longitude || 0,
    inpeCode: profile.inpeCode || '',
    isVerified: profile.isVerified || false,
    internalUser: profile.internalUser,
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
      await axios.patch(`/api/doctor-profiles/${profile.id}`, formData, {
        headers: {
          'Content-Type': 'application/merge-patch+json',
        },
      });
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
            />
          </FormGroup>

          <FormGroup>
            <Label for="specialization">
              <Translate contentKey="account.manage.doctorProfile.specialization">Specialization</Translate>
            </Label>
            <Input
              type="text"
              name="specialization"
              id="specialization"
              value={formData.specialization}
              onChange={handleChange}
              placeholder="account.manage.doctorProfile.specialization.placeholder"
            />
          </FormGroup>

          <FormGroup>
            <Label for="licenseNumber">
              <Translate contentKey="account.manage.doctorProfile.licenseNumber">License Number</Translate>
            </Label>
            <Input
              type="text"
              name="licenseNumber"
              id="licenseNumber"
              value={formData.licenseNumber}
              onChange={handleChange}
              placeholder="account.manage.doctorProfile.licenseNumber.placeholder"
            />
          </FormGroup>

          <FormGroup>
            <Label for="yearsOfExperience">
              <Translate contentKey="account.manage.doctorProfile.yearsOfExperience">Years of Experience</Translate>
            </Label>
            <Input
              type="number"
              name="yearsOfExperience"
              id="yearsOfExperience"
              value={formData.yearsOfExperience}
              onChange={handleChange}
              placeholder="account.manage.doctorProfile.yearsOfExperience.placeholder"
            />
          </FormGroup>

          <FormGroup>
            <Label for="consultationFee">
              <Translate contentKey="account.manage.doctorProfile.consultationFee">Consultation Fee</Translate>
            </Label>
            <Input
              type="number"
              name="consultationFee"
              id="consultationFee"
              value={formData.consultationFee}
              onChange={handleChange}
              placeholder="account.manage.doctorProfile.consultationFee.placeholder"
            />
          </FormGroup>

          <FormGroup>
            <Label for="workingHours">
              <Translate contentKey="account.manage.doctorProfile.workingHours">Working Hours</Translate>
            </Label>
            <Input
              type="text"
              name="workingHours"
              id="workingHours"
              value={formData.workingHours}
              onChange={handleChange}
              placeholder="account.manage.doctorProfile.workingHours.placeholder"
            />
          </FormGroup>

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
