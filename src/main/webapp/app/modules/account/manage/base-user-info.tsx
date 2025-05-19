import React, { useState } from 'react';
import { CardHeader, CardBody, Form, FormGroup, Label, Input, Button, Alert } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Translate } from 'react-jhipster';
import axios from 'axios';

interface BaseUserInfoProps {
  account: any;
}

const BaseUserInfo: React.FC<BaseUserInfoProps> = ({ account }) => {
  const [formData, setFormData] = useState({
    id: account.id || '',
    login: account.login || '',
    firstName: account.firstName || '',
    lastName: account.lastName || '',
    email: account.email || '',
    imageUrl: account.imageUrl || '',
    activated: account.activated || false,
    langKey: account.langKey || 'en',
    authorities: account.authorities || [],
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
      await axios.put('/api/account', formData);
      setSuccess(true);
    } catch (err) {
      setError('account.manage.baseInfo.error');
      console.error('Error updating account:', err);
    }
  };

  return (
    <>
      <CardHeader>
        <h3>
          <FontAwesomeIcon icon="user" />
          <Translate contentKey="account.manage.baseInfo.title">Basic Information</Translate>
        </h3>
      </CardHeader>
      <CardBody>
        {success && (
          <Alert color="success">
            <Translate contentKey="account.manage.baseInfo.success">Account information updated successfully</Translate>
          </Alert>
        )}
        {error && (
          <Alert color="danger">
            <Translate contentKey={error}>Error updating account information</Translate>
          </Alert>
        )}

        <Form onSubmit={handleSubmit}>
          <FormGroup>
            <Label for="firstName">
              <Translate contentKey="account.manage.baseInfo.firstName">First Name</Translate>
            </Label>
            <Input
              type="text"
              name="firstName"
              id="firstName"
              value={formData.firstName}
              onChange={handleChange}
              placeholder="account.manage.baseInfo.firstName.placeholder"
              maxLength={50}
            />
          </FormGroup>

          <FormGroup>
            <Label for="lastName">
              <Translate contentKey="account.manage.baseInfo.lastName">Last Name</Translate>
            </Label>
            <Input
              type="text"
              name="lastName"
              id="lastName"
              value={formData.lastName}
              onChange={handleChange}
              placeholder="account.manage.baseInfo.lastName.placeholder"
              maxLength={50}
            />
          </FormGroup>

          <FormGroup>
            <Label for="email">
              <Translate contentKey="account.manage.baseInfo.email">Email</Translate>
            </Label>
            <Input
              type="email"
              name="email"
              id="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="account.manage.baseInfo.email.placeholder"
              maxLength={254}
            />
          </FormGroup>

          <FormGroup>
            <Label for="langKey">
              <Translate contentKey="account.manage.baseInfo.language">Language</Translate>
            </Label>
            <Input type="select" name="langKey" id="langKey" value={formData.langKey} onChange={handleChange}>
              <option value="en">English</option>
              <option value="fr">Français</option>
              <option value="de">Deutsch</option>
              <option value="ar-ly">العربية</option>
            </Input>
          </FormGroup>

          <Button type="submit" color="primary">
            <FontAwesomeIcon icon="save" className="me-2" />
            <Translate contentKey="account.manage.baseInfo.save">Save Changes</Translate>
          </Button>
        </Form>
      </CardBody>
    </>
  );
};

export default BaseUserInfo;
