import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAppSelector } from 'app/config/store';
import './styles/doctor-dashboard.scss';

interface DoctorAvailability {
  id?: number;
  dayOfWeek: string;
  startTime: string;
  endTime: string;
  isAvailable: boolean;
  recurrenceType: string;
  validFrom: string;
  validTo?: string;
  doctorId?: string;
}

interface DoctorDocument {
  id?: number;
  type: string;
  fileName: string;
  fileContent?: string;
  fileContentContentType?: string;
  uploadDate?: string;
  verificationStatus: string;
  doctorId?: string;
}

interface Specialization {
  id: number;
  name: string;
  description?: string;
}

const DoctorDashboard: React.FC = () => {
  const account = useAppSelector(state => state.authentication.account);
  const [activeTab, setActiveTab] = useState<'availability' | 'documents' | 'specializations'>('availability');
  const [availabilities, setAvailabilities] = useState<DoctorAvailability[]>([]);
  const [documents, setDocuments] = useState<DoctorDocument[]>([]);
  const [specializations, setSpecializations] = useState<Specialization[]>([]);
  const [availableSpecializations, setAvailableSpecializations] = useState<Specialization[]>([]);
  const [loading, setLoading] = useState(true);
  const [editingAvailability, setEditingAvailability] = useState<DoctorAvailability | null>(null);
  const [showAddForm, setShowAddForm] = useState(false);
  const [showDocumentForm, setShowDocumentForm] = useState(false);
  const [showSpecializationForm, setShowSpecializationForm] = useState(false);
  const [doctorProfile, setDoctorProfile] = useState<any>(null);

  const daysOfWeek = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];
  const recurrenceTypes = ['ONCE', 'WEEKLY', 'MONTHLY', 'YEARLY'];
  const documentTypes = ['DIPLOMA', 'CV', 'LICENSE', 'OTHER'];
  const verificationStatuses = ['PENDING', 'VERIFIED', 'REJECTED'];

  useEffect(() => {
    loadDoctorProfile();
  }, [account]);

  useEffect(() => {
    if (doctorProfile) {
      if (activeTab === 'availability') {
        loadAvailabilities();
      } else if (activeTab === 'documents') {
        loadDocuments();
      } else if (activeTab === 'specializations') {
        loadSpecializations();
        loadAvailableSpecializations();
      }
    }
  }, [doctorProfile, activeTab]);

  const loadDoctorProfile = async () => {
    try {
      if (account?.id) {
        const response = await axios.get('/api/doctor-profiles/current');
        setDoctorProfile(response.data);
      }
    } catch (error) {
      console.error('Error loading doctor profile:', error);
    }
  };

  const loadAvailabilities = async () => {
    try {
      setLoading(true);
      if (doctorProfile?.id) {
        const response = await axios.get(`/api/doctor-availabilities/doctor/${doctorProfile.id}`);
        setAvailabilities(response.data);
      }
    } catch (error) {
      console.error('Error loading availabilities:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadDocuments = async () => {
    try {
      setLoading(true);
      if (doctorProfile?.id) {
        const response = await axios.get(`/api/doctor-documents/doctor/${doctorProfile.id}`);
        setDocuments(response.data);
      }
    } catch (error) {
      console.error('Error loading documents:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSaveAvailability = async (availability: DoctorAvailability) => {
    try {
      const availabilityData = {
        ...availability,
        doctorId: doctorProfile.id,
      };

      if (availability.id) {
        await axios.put(`/api/doctor-availabilities/${availability.id}`, availabilityData);
      } else {
        await axios.post('/api/doctor-availabilities', availabilityData);
      }

      loadAvailabilities();
      setEditingAvailability(null);
      setShowAddForm(false);
    } catch (error) {
      console.error('Error saving availability:', error);
    }
  };

  const handleDeleteAvailability = async (id: number) => {
    try {
      await axios.delete(`/api/doctor-availabilities/${id}`);
      loadAvailabilities();
    } catch (error) {
      console.error('Error deleting availability:', error);
    }
  };

  const handleSaveDocument = async (document: DoctorDocument, file: File) => {
    try {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('type', document.type);
      formData.append('doctorId', doctorProfile.id);

      // Convert file to base64 for the API
      const fileContent = await new Promise<string>(resolve => {
        const reader = new FileReader();
        reader.onload = () => resolve(reader.result as string);
        reader.readAsDataURL(file);
      });

      const documentData = {
        type: document.type,
        fileName: file.name,
        fileContent: fileContent.split(',')[1], // Remove data:type;base64, prefix
        fileContentContentType: file.type,
        uploadDate: new Date().toISOString(),
        verificationStatus: 'PENDING',
        doctor: { id: doctorProfile.id },
      };

      await axios.post('/api/doctor-documents', documentData);
      loadDocuments();
      setShowDocumentForm(false);
    } catch (error) {
      console.error('Error saving document:', error);
    }
  };

  const handleDeleteDocument = async (id: number) => {
    try {
      await axios.delete(`/api/doctor-documents/${id}`);
      loadDocuments();
    } catch (error) {
      console.error('Error deleting document:', error);
    }
  };

  const loadSpecializations = () => {
    try {
      setLoading(true);
      if (doctorProfile?.specializations) {
        setSpecializations(doctorProfile.specializations);
      }
    } catch (error) {
      console.error('Error loading specializations:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadAvailableSpecializations = async () => {
    try {
      const response = await axios.get('/api/specializations?size=1000');
      setAvailableSpecializations(response.data);
    } catch (error) {
      console.error('Error loading available specializations:', error);
    }
  };

  const handleAddSpecialization = async (specializationId: number) => {
    try {
      if (!doctorProfile) return;

      const updatedSpecializations = [...specializations];
      const specializationToAdd = availableSpecializations.find(s => s.id === specializationId);

      if (specializationToAdd && !updatedSpecializations.find(s => s.id === specializationId)) {
        updatedSpecializations.push(specializationToAdd);

        const updatedProfile = {
          ...doctorProfile,
          specializations: updatedSpecializations.map(s => ({ id: s.id })),
        };

        await axios.put(`/api/doctor-profiles/${doctorProfile.id}`, updatedProfile);
        setSpecializations(updatedSpecializations);
        setDoctorProfile({ ...doctorProfile, specializations: updatedSpecializations });
        setShowSpecializationForm(false);
      }
    } catch (error) {
      console.error('Error adding specialization:', error);
    }
  };

  const handleRemoveSpecialization = async (specializationId: number) => {
    try {
      if (!doctorProfile) return;

      const updatedSpecializations = specializations.filter(s => s.id !== specializationId);

      const updatedProfile = {
        ...doctorProfile,
        specializations: updatedSpecializations.map(s => ({ id: s.id })),
      };

      await axios.put(`/api/doctor-profiles/${doctorProfile.id}`, updatedProfile);
      setSpecializations(updatedSpecializations);
      setDoctorProfile({ ...doctorProfile, specializations: updatedSpecializations });
    } catch (error) {
      console.error('Error removing specialization:', error);
    }
  };

  const AvailabilityForm: React.FC<{
    availability: DoctorAvailability | null;
    onSave: (availability: DoctorAvailability) => void;
    onCancel: () => void;
  }> = ({ availability, onSave, onCancel }) => {
    const [formData, setFormData] = useState<DoctorAvailability>(
      availability || {
        dayOfWeek: 'MONDAY',
        startTime: '09:00',
        endTime: '17:00',
        isAvailable: true,
        recurrenceType: 'WEEKLY',
        validFrom: new Date().toISOString().split('T')[0],
      },
    );

    const handleSubmit = (e: React.FormEvent) => {
      e.preventDefault();
      onSave(formData);
    };

    return (
      <div className="availability-form">
        <form onSubmit={handleSubmit}>
          <div className="row">
            <div className="col-md-6 mb-3">
              <label className="form-label">Day of Week</label>
              <select
                className="form-select"
                value={formData.dayOfWeek}
                onChange={e => setFormData({ ...formData, dayOfWeek: e.target.value })}
                required
              >
                {daysOfWeek.map(day => (
                  <option key={day} value={day}>
                    {day}
                  </option>
                ))}
              </select>
            </div>
            <div className="col-md-6 mb-3">
              <label className="form-label">Recurrence</label>
              <select
                className="form-select"
                value={formData.recurrenceType}
                onChange={e => setFormData({ ...formData, recurrenceType: e.target.value })}
                required
              >
                {recurrenceTypes.map(type => (
                  <option key={type} value={type}>
                    {type}
                  </option>
                ))}
              </select>
            </div>
          </div>
          <div className="row">
            <div className="col-md-6 mb-3">
              <label className="form-label">Start Time</label>
              <input
                type="time"
                className="form-control"
                value={formData.startTime}
                onChange={e => setFormData({ ...formData, startTime: e.target.value })}
                required
              />
            </div>
            <div className="col-md-6 mb-3">
              <label className="form-label">End Time</label>
              <input
                type="time"
                className="form-control"
                value={formData.endTime}
                onChange={e => setFormData({ ...formData, endTime: e.target.value })}
                required
              />
            </div>
          </div>
          <div className="row">
            <div className="col-md-6 mb-3">
              <label className="form-label">Valid From</label>
              <input
                type="date"
                className="form-control"
                value={formData.validFrom}
                onChange={e => setFormData({ ...formData, validFrom: e.target.value })}
                required
              />
            </div>
            <div className="col-md-6 mb-3">
              <label className="form-label">Valid To (Optional)</label>
              <input
                type="date"
                className="form-control"
                value={formData.validTo || ''}
                onChange={e => setFormData({ ...formData, validTo: e.target.value })}
              />
            </div>
          </div>
          <div className="mb-3">
            <div className="form-check">
              <input
                type="checkbox"
                className="form-check-input"
                id="isAvailable"
                checked={formData.isAvailable}
                onChange={e => setFormData({ ...formData, isAvailable: e.target.checked })}
              />
              <label className="form-check-label" htmlFor="isAvailable">
                Available for appointments
              </label>
            </div>
          </div>
          <div className="d-flex gap-2">
            <button type="submit" className="btn btn-primary">
              <i className="fas fa-save me-2"></i>
              Save
            </button>
            <button type="button" className="btn btn-secondary" onClick={onCancel}>
              <i className="fas fa-times me-2"></i>
              Cancel
            </button>
          </div>
        </form>
      </div>
    );
  };

  const DocumentForm: React.FC<{
    onSave: (document: DoctorDocument, file: File) => void;
    onCancel: () => void;
  }> = ({ onSave, onCancel }) => {
    const [formData, setFormData] = useState<DoctorDocument>({
      type: 'CV',
      fileName: '',
      verificationStatus: 'PENDING',
    });
    const [selectedFile, setSelectedFile] = useState<File | null>(null);

    const handleSubmit = (e: React.FormEvent) => {
      e.preventDefault();
      if (selectedFile) {
        onSave(formData, selectedFile);
      }
    };

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
      const file = e.target.files?.[0];
      if (file) {
        setSelectedFile(file);
        setFormData({ ...formData, fileName: file.name });
      }
    };

    return (
      <div className="availability-form">
        <form onSubmit={handleSubmit}>
          <div className="row">
            <div className="col-md-6 mb-3">
              <label className="form-label">Document Type</label>
              <select
                className="form-select"
                value={formData.type}
                onChange={e => setFormData({ ...formData, type: e.target.value })}
                required
              >
                {documentTypes.map(type => (
                  <option key={type} value={type}>
                    {type}
                  </option>
                ))}
              </select>
            </div>
            <div className="col-md-6 mb-3">
              <label className="form-label">File</label>
              <input type="file" className="form-control" onChange={handleFileChange} accept=".pdf,.doc,.docx,.jpg,.jpeg,.png" required />
              <small className="form-text text-muted">Accepted formats: PDF, DOC, DOCX, JPG, PNG (Max 10MB)</small>
            </div>
          </div>
          <div className="d-flex gap-2">
            <button type="submit" className="btn btn-primary" disabled={!selectedFile}>
              <i className="fas fa-upload me-2"></i>
              Upload Document
            </button>
            <button type="button" className="btn btn-secondary" onClick={onCancel}>
              <i className="fas fa-times me-2"></i>
              Cancel
            </button>
          </div>
        </form>
      </div>
    );
  };

  const SpecializationForm: React.FC<{
    onAdd: (specializationId: number) => void;
    onCancel: () => void;
  }> = ({ onAdd, onCancel }) => {
    const [selectedSpecializationId, setSelectedSpecializationId] = useState<number | null>(null);

    const handleSubmit = (e: React.FormEvent) => {
      e.preventDefault();
      if (selectedSpecializationId) {
        onAdd(selectedSpecializationId);
      }
    };

    const availableToAdd = availableSpecializations.filter(spec => !specializations.find(s => s.id === spec.id));

    return (
      <div className="availability-form">
        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label className="form-label">Select Specialization</label>
            <select
              className="form-select"
              value={selectedSpecializationId || ''}
              onChange={e => setSelectedSpecializationId(Number(e.target.value))}
              required
            >
              <option value="">Choose a specialization...</option>
              {availableToAdd.map(specialization => (
                <option key={specialization.id} value={specialization.id}>
                  {specialization.name}
                  {specialization.description && ` - ${specialization.description}`}
                </option>
              ))}
            </select>
            {availableToAdd.length === 0 && <small className="form-text text-muted">All available specializations have been added.</small>}
          </div>
          <div className="d-flex gap-2">
            <button type="submit" className="btn btn-primary" disabled={!selectedSpecializationId}>
              <i className="fas fa-plus me-2"></i>
              Add Specialization
            </button>
            <button type="button" className="btn btn-secondary" onClick={onCancel}>
              <i className="fas fa-times me-2"></i>
              Cancel
            </button>
          </div>
        </form>
      </div>
    );
  };

  if (!doctorProfile) {
    return (
      <div className="card shadow-sm h-100">
        <div className="card-body">
          <div className="text-center">
            <i className="fas fa-user-md fa-3x text-muted mb-3"></i>
            <h4>Doctor Profile Required</h4>
            <p className="text-muted">Please complete your doctor profile to access the dashboard.</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="card shadow-sm h-100 doctor-dashboard">
      <div className="card-body">
        <div className="d-flex justify-content-between align-items-center mb-4">
          <h3 className="card-title mb-0">
            <i className="fas fa-user-md me-2"></i>
            Doctor Dashboard
          </h3>
        </div>

        {/* Tab Navigation */}
        <ul className="nav nav-tabs mb-4">
          <li className="nav-item">
            <button className={`nav-link ${activeTab === 'availability' ? 'active' : ''}`} onClick={() => setActiveTab('availability')}>
              <i className="fas fa-calendar-alt me-2"></i>
              Availability Management
            </button>
          </li>
          <li className="nav-item">
            <button className={`nav-link ${activeTab === 'documents' ? 'active' : ''}`} onClick={() => setActiveTab('documents')}>
              <i className="fas fa-file-medical me-2"></i>
              Document Submission
            </button>
          </li>
          <li className="nav-item">
            <button
              className={`nav-link ${activeTab === 'specializations' ? 'active' : ''}`}
              onClick={() => setActiveTab('specializations')}
            >
              <i className="fas fa-stethoscope me-2"></i>
              Specializations
            </button>
          </li>
        </ul>

        {/* Tab Content */}
        {activeTab === 'availability' && (
          <div className="tab-content">
            <div className="d-flex justify-content-between align-items-center mb-3">
              <h5 className="mb-0">Manage Your Availability</h5>
              <button className="btn btn-primary" onClick={() => setShowAddForm(true)} disabled={showAddForm || !!editingAvailability}>
                <i className="fas fa-plus me-2"></i>
                Add Availability
              </button>
            </div>

            {showAddForm && (
              <div className="action-card mb-4">
                <h5 className="mb-3">
                  <i className="fas fa-plus me-2"></i>
                  Add New Availability
                </h5>
                <AvailabilityForm availability={null} onSave={handleSaveAvailability} onCancel={() => setShowAddForm(false)} />
              </div>
            )}

            {editingAvailability && (
              <div className="action-card mb-4">
                <h5 className="mb-3">
                  <i className="fas fa-edit me-2"></i>
                  Edit Availability
                </h5>
                <AvailabilityForm
                  availability={editingAvailability}
                  onSave={handleSaveAvailability}
                  onCancel={() => setEditingAvailability(null)}
                />
              </div>
            )}

            <div className="availability-list">
              {loading ? (
                <div className="text-center py-4">
                  <i className="fas fa-spinner fa-spin fa-2x text-muted"></i>
                  <p className="text-muted mt-2">Loading availabilities...</p>
                </div>
              ) : availabilities.length === 0 ? (
                <div className="text-center py-4">
                  <i className="fas fa-calendar-times fa-3x text-muted mb-3"></i>
                  <h5>No Availability Set</h5>
                  <p className="text-muted">Add your first availability to start managing your schedule.</p>
                </div>
              ) : (
                <div className="row">
                  {availabilities.map(availability => (
                    <div key={availability.id} className="col-md-6 mb-3">
                      <div className="stat-card">
                        <div className="d-flex justify-content-between align-items-start mb-2">
                          <div className="stat-icon">
                            <i className={`fas ${availability.isAvailable ? 'fa-check-circle' : 'fa-times-circle'}`}></i>
                          </div>
                          <div className="card-actions">
                            <button
                              className="btn btn-sm btn-primary me-1"
                              onClick={() => setEditingAvailability(availability)}
                              disabled={showAddForm || !!editingAvailability}
                            >
                              <i className="fas fa-edit me-1"></i>
                              Edit
                            </button>
                            <button
                              className="btn btn-sm btn-danger"
                              onClick={() => availability.id && handleDeleteAvailability(availability.id)}
                            >
                              <i className="fas fa-trash me-1"></i>
                              Delete
                            </button>
                          </div>
                        </div>
                        <div className="stat-info">
                          <h4>{availability.dayOfWeek}</h4>
                          <p className="stat-value">
                            {availability.startTime} - {availability.endTime}
                          </p>
                          <p className="stat-label">
                            {availability.recurrenceType} •{availability.isAvailable ? ' Available' : ' Unavailable'}
                          </p>
                          <small className="text-muted">
                            From {availability.validFrom}
                            {availability.validTo && ` to ${availability.validTo}`}
                          </small>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        )}

        {activeTab === 'documents' && (
          <div className="tab-content">
            <div className="d-flex justify-content-between align-items-center mb-3">
              <h5 className="mb-0">Submit Documents for Verification</h5>
              <button className="btn btn-primary" onClick={() => setShowDocumentForm(true)} disabled={showDocumentForm}>
                <i className="fas fa-upload me-2"></i>
                Upload Document
              </button>
            </div>

            {showDocumentForm && (
              <div className="action-card mb-4">
                <h5 className="mb-3">
                  <i className="fas fa-upload me-2"></i>
                  Upload New Document
                </h5>
                <DocumentForm onSave={handleSaveDocument} onCancel={() => setShowDocumentForm(false)} />
              </div>
            )}

            <div className="documents-list">
              {loading ? (
                <div className="text-center py-4">
                  <i className="fas fa-spinner fa-spin fa-2x text-muted"></i>
                  <p className="text-muted mt-2">Loading documents...</p>
                </div>
              ) : documents.length === 0 ? (
                <div className="text-center py-4">
                  <i className="fas fa-file-upload fa-3x text-muted mb-3"></i>
                  <h5>No Documents Uploaded</h5>
                  <p className="text-muted">Upload your first document to start the verification process.</p>
                </div>
              ) : (
                <div className="row">
                  {documents.map(document => (
                    <div key={document.id} className="col-md-6 mb-3">
                      <div className="stat-card">
                        <div className="d-flex justify-content-between align-items-start mb-2">
                          <div className="stat-icon">
                            <i
                              className={`fas ${
                                document.verificationStatus === 'VERIFIED'
                                  ? 'fa-check-circle'
                                  : document.verificationStatus === 'REJECTED'
                                    ? 'fa-times-circle'
                                    : 'fa-clock'
                              }`}
                            ></i>
                          </div>
                          <div className="card-actions">
                            <button className="btn btn-sm btn-danger" onClick={() => document.id && handleDeleteDocument(document.id)}>
                              <i className="fas fa-trash me-1"></i>
                              Delete
                            </button>
                          </div>
                        </div>
                        <div className="stat-info">
                          <h4>{document.type}</h4>
                          <p className="stat-value">{document.fileName}</p>
                          <p className="stat-label">
                            Status:{' '}
                            <span
                              className={`badge ${
                                document.verificationStatus === 'VERIFIED'
                                  ? 'bg-success'
                                  : document.verificationStatus === 'REJECTED'
                                    ? 'bg-danger'
                                    : 'bg-warning'
                              }`}
                            >
                              {document.verificationStatus}
                            </span>
                          </p>
                          {document.uploadDate && (
                            <small className="text-muted">Uploaded: {new Date(document.uploadDate).toLocaleDateString()}</small>
                          )}
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        )}

        {activeTab === 'specializations' && (
          <div className="tab-content">
            <div className="d-flex justify-content-between align-items-center mb-3">
              <h5 className="mb-0">Manage Your Specializations</h5>
              <button
                className="btn btn-primary"
                onClick={() => setShowSpecializationForm(true)}
                disabled={
                  showSpecializationForm ||
                  availableSpecializations.filter(spec => !specializations.find(s => s.id === spec.id)).length === 0
                }
              >
                <i className="fas fa-plus me-2"></i>
                Add Specialization
              </button>
            </div>

            {showSpecializationForm && (
              <div className="action-card mb-4">
                <h5 className="mb-3">
                  <i className="fas fa-plus me-2"></i>
                  Add New Specialization
                </h5>
                <SpecializationForm onAdd={handleAddSpecialization} onCancel={() => setShowSpecializationForm(false)} />
              </div>
            )}

            <div className="specializations-list">
              {loading ? (
                <div className="text-center py-4">
                  <i className="fas fa-spinner fa-spin fa-2x text-muted"></i>
                  <p className="text-muted mt-2">Loading specializations...</p>
                </div>
              ) : specializations.length === 0 ? (
                <div className="text-center py-4">
                  <i className="fas fa-stethoscope fa-3x text-muted mb-3"></i>
                  <h5>No Specializations Added</h5>
                  <p className="text-muted">Add your first specialization to showcase your medical expertise.</p>
                </div>
              ) : (
                <div className="row">
                  {specializations.map(specialization => (
                    <div key={specialization.id} className="col-md-6 mb-3">
                      <div className="stat-card">
                        <div className="d-flex justify-content-between align-items-start mb-2">
                          <div className="stat-icon">
                            <i className="fas fa-stethoscope"></i>
                          </div>
                          <div className="card-actions">
                            <button className="btn btn-sm btn-danger" onClick={() => handleRemoveSpecialization(specialization.id)}>
                              <i className="fas fa-trash me-1"></i>
                              Remove
                            </button>
                          </div>
                        </div>
                        <div className="stat-info">
                          <h4>{specialization.name}</h4>
                          {specialization.description && <p className="stat-value">{specialization.description}</p>}
                          <p className="stat-label">Medical Specialization</p>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default DoctorDashboard;
