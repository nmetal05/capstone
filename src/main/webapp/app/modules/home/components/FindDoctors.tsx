import React, { useState, useEffect } from 'react';
import { Button, Form, FormGroup, Label, Input } from 'reactstrap';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './styles/find-doctors.scss';

interface Specialization {
  id: number;
  name: string;
  description: string;
}

const FindDoctors: React.FC = () => {
  const navigate = useNavigate();
  const [specializations, setSpecializations] = useState<Specialization[]>([]);
  const [selectedSpec, setSelectedSpec] = useState('');
  const [radius, setRadius] = useState(10);
  const [openNowOnly, setOpenNowOnly] = useState(false);
  const [sortBy, setSortBy] = useState('distance');
  const [sortDir, setSortDir] = useState('asc');

  useEffect(() => {
    // Load specializations
    axios
      .get('/api/specializations?page=0&size=100')
      .then(response => {
        setSpecializations(response.data);
      })
      .catch(err => {
        console.error('Failed to load specializations', err);
      });
  }, []);

  const handleFindDoctors = () => {
    if (!selectedSpec) {
      return;
    }

    // Get user location (simplified - in real app you'd use geolocation API)
    const lat = 50.0; // Default latitude
    const lon = 30.0; // Default longitude

    navigate(
      `/doctors/nearby?spec=${encodeURIComponent(selectedSpec)}&lat=${lat}&lon=${lon}&radius=${radius}&sort=${sortBy}&dir=${sortDir}&openNow=${openNowOnly}`,
    );
  };

  return (
    <div className="card shadow-sm h-100">
      <div className="card-body">
        <h3 className="card-title mb-4">
          <i className="fas fa-map-marker-alt me-2"></i>
          Find Nearby Doctors
        </h3>
        <p className="text-muted mb-4">Search for medical specialists in your area with specific criteria.</p>

        <Form>
          <FormGroup>
            <Label for="specialization">Specialization</Label>
            <Input type="select" id="specialization" value={selectedSpec} onChange={e => setSelectedSpec(e.target.value)}>
              <option value="">Select a specialization</option>
              {specializations.map(spec => (
                <option key={spec.id} value={spec.name}>
                  {spec.name}
                </option>
              ))}
            </Input>
          </FormGroup>

          <FormGroup>
            <Label for="radius">Search Radius: {radius} km</Label>
            <div className="d-flex align-items-center">
              <input
                type="range"
                className="form-range flex-grow-1 me-3"
                id="radius"
                min={1}
                max={50}
                step={1}
                value={radius}
                onChange={e => setRadius(parseInt(e.target.value, 10))}
              />
              <span className="badge bg-primary">{radius} km</span>
            </div>
          </FormGroup>

          <FormGroup check>
            <Input type="checkbox" id="openNow" checked={openNowOnly} onChange={e => setOpenNowOnly(e.target.checked)} />
            <Label for="openNow" check>
              Only show currently open
            </Label>
          </FormGroup>

          <FormGroup>
            <Label for="sortBy">Sort By</Label>
            <div className="d-flex">
              <Input type="select" id="sortBy" value={sortBy} onChange={e => setSortBy(e.target.value)} className="me-2">
                <option value="distance">Distance</option>
                <option value="rating">Rating</option>
              </Input>
              <Input type="select" value={sortDir} onChange={e => setSortDir(e.target.value)}>
                <option value="asc">Ascending</option>
                <option value="desc">Descending</option>
              </Input>
            </div>
          </FormGroup>

          <Button color="primary" onClick={handleFindDoctors} className="w-100 py-2 mt-3" disabled={!selectedSpec}>
            <i className="fas fa-search-location me-2"></i>
            Find Doctors
          </Button>
        </Form>
      </div>
    </div>
  );
};

export default FindDoctors;
