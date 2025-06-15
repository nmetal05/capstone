import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './admin-dashboard.scss';

interface DoctorDocument {
  id: number;
  type: string;
  fileName: string;
  fileContent?: string;
  fileContentContentType?: string;
  uploadDate: string;
  verificationStatus: string;
  doctor: {
    id: string;
    internalUser?: {
      login: string;
      firstName?: string;
      lastName?: string;
    };
  };
}

const AdminDashboard: React.FC = () => {
  const [documents, setDocuments] = useState<DoctorDocument[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState<'ALL' | 'PENDING' | 'VERIFIED' | 'REJECTED'>('PENDING');
  const [showCommentModal, setShowCommentModal] = useState(false);
  const [selectedDocument, setSelectedDocument] = useState<DoctorDocument | null>(null);
  const [comment, setComment] = useState('');
  const [actionType, setActionType] = useState<'VERIFIED' | 'REJECTED'>('REJECTED');

  useEffect(() => {
    loadDocuments();
  }, []);

  const loadDocuments = async () => {
    try {
      setLoading(true);
      const response = await axios.get('/api/doctor-documents');
      setDocuments(response.data);
    } catch (error) {
      console.error('Error loading documents:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleVerifyDocument = async (documentId: number, status: 'VERIFIED' | 'REJECTED' | 'PENDING', adminComment?: string) => {
    try {
      if (status === 'REJECTED' && !adminComment) {
        // Show modal for rejection comment
        const document = documents.find(doc => doc.id === documentId);
        if (document) {
          setSelectedDocument(document);
          setActionType('REJECTED');
          setShowCommentModal(true);
        }
        return;
      }

      // Use the new verification endpoint that sends emails
      const verificationRequest = {
        status,
        comment: adminComment || '',
      };

      await axios.post(`/api/doctor-documents/${documentId}/verify`, verificationRequest);
      loadDocuments();
    } catch (error) {
      console.error('Error updating document status:', error);
    }
  };

  const handleModalSubmit = async () => {
    if (selectedDocument) {
      await handleVerifyDocument(selectedDocument.id, actionType, comment);
      setShowCommentModal(false);
      setSelectedDocument(null);
      setComment('');
    }
  };

  const handleModalCancel = () => {
    setShowCommentModal(false);
    setSelectedDocument(null);
    setComment('');
  };

  const downloadDocument = (doc: DoctorDocument) => {
    if (doc.fileContent && doc.fileContentContentType) {
      const byteCharacters = atob(doc.fileContent);
      const byteNumbers = new Array(byteCharacters.length);
      for (let i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i);
      }
      const byteArray = new Uint8Array(byteNumbers);
      const blob = new Blob([byteArray], { type: doc.fileContentContentType });

      const url = window.URL.createObjectURL(blob);
      const link = window.document.createElement('a');
      link.href = url;
      link.download = doc.fileName;
      window.document.body.appendChild(link);
      link.click();
      window.document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    }
  };

  const filteredDocuments = documents.filter(doc => filter === 'ALL' || doc.verificationStatus === filter);

  const getStatusCounts = () => {
    return {
      pending: documents.filter(doc => doc.verificationStatus === 'PENDING').length,
      verified: documents.filter(doc => doc.verificationStatus === 'VERIFIED').length,
      rejected: documents.filter(doc => doc.verificationStatus === 'REJECTED').length,
      total: documents.length,
    };
  };

  const statusCounts = getStatusCounts();

  return (
    <div className="card shadow-sm h-100 admin-dashboard">
      <div className="card-body">
        <div className="d-flex justify-content-between align-items-center mb-4">
          <h3 className="card-title mb-0">
            <i className="fas fa-shield-alt me-2"></i>
            Admin Dashboard - Document Verification
          </h3>
        </div>

        {/* Statistics Cards */}
        <div className="row mb-4">
          <div className="col-md-3 mb-3">
            <div className="stat-card stats-card">
              <div className="stat-icon">
                <i className="fas fa-clock text-warning"></i>
              </div>
              <div className="stat-info">
                <h4>{statusCounts.pending}</h4>
                <p className="stat-label">Pending Review</p>
              </div>
            </div>
          </div>
          <div className="col-md-3 mb-3">
            <div className="stat-card stats-card">
              <div className="stat-icon">
                <i className="fas fa-check-circle text-success"></i>
              </div>
              <div className="stat-info">
                <h4>{statusCounts.verified}</h4>
                <p className="stat-label">Verified</p>
              </div>
            </div>
          </div>
          <div className="col-md-3 mb-3">
            <div className="stat-card stats-card">
              <div className="stat-icon">
                <i className="fas fa-times-circle text-danger"></i>
              </div>
              <div className="stat-info">
                <h4>{statusCounts.rejected}</h4>
                <p className="stat-label">Rejected</p>
              </div>
            </div>
          </div>
          <div className="col-md-3 mb-3">
            <div className="stat-card stats-card">
              <div className="stat-icon">
                <i className="fas fa-file-medical text-primary"></i>
              </div>
              <div className="stat-info">
                <h4>{statusCounts.total}</h4>
                <p className="stat-label">Total Documents</p>
              </div>
            </div>
          </div>
        </div>

        {/* Filter Buttons */}
        <div className="d-flex gap-2 mb-4">
          <button className={`btn ${filter === 'ALL' ? 'btn-primary' : 'btn-outline-primary'}`} onClick={() => setFilter('ALL')}>
            All ({statusCounts.total})
          </button>
          <button className={`btn ${filter === 'PENDING' ? 'btn-warning' : 'btn-outline-warning'}`} onClick={() => setFilter('PENDING')}>
            Pending ({statusCounts.pending})
          </button>
          <button className={`btn ${filter === 'VERIFIED' ? 'btn-success' : 'btn-outline-success'}`} onClick={() => setFilter('VERIFIED')}>
            Verified ({statusCounts.verified})
          </button>
          <button className={`btn ${filter === 'REJECTED' ? 'btn-danger' : 'btn-outline-danger'}`} onClick={() => setFilter('REJECTED')}>
            Rejected ({statusCounts.rejected})
          </button>
        </div>

        {/* Documents List */}
        <div className="documents-list">
          {loading ? (
            <div className="text-center py-4">
              <i className="fas fa-spinner fa-spin fa-2x text-muted"></i>
              <p className="text-muted mt-2">Loading documents...</p>
            </div>
          ) : filteredDocuments.length === 0 ? (
            <div className="text-center py-4">
              <i className="fas fa-file-upload fa-3x text-muted mb-3"></i>
              <h5>No Documents Found</h5>
              <p className="text-muted">No documents match the current filter.</p>
            </div>
          ) : (
            <div className="row">
              {filteredDocuments.map(document => (
                <div key={document.id} className="col-md-6 col-lg-4 mb-3">
                  <div className="stat-card document-card">
                    <div className="d-flex justify-content-between align-items-start mb-2">
                      <div className="stat-icon">
                        <i
                          className={`fas ${
                            document.verificationStatus === 'VERIFIED'
                              ? 'fa-check-circle text-success'
                              : document.verificationStatus === 'REJECTED'
                                ? 'fa-times-circle text-danger'
                                : 'fa-clock text-warning'
                          }`}
                        ></i>
                      </div>
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
                    </div>

                    <div className="stat-info">
                      <h4>{document.type}</h4>
                      <p className="stat-value">{document.fileName}</p>
                      <p className="stat-label">
                        Doctor: {document.doctor.internalUser?.firstName} {document.doctor.internalUser?.lastName}
                        <br />
                        <small className="text-muted">({document.doctor.internalUser?.login})</small>
                      </p>
                      <small className="text-muted">Uploaded: {new Date(document.uploadDate).toLocaleDateString()}</small>
                    </div>

                    <div className="mt-3 d-flex gap-2 flex-wrap">
                      <button className="btn btn-sm btn-outline-primary" onClick={() => downloadDocument(document)}>
                        <i className="fas fa-download me-1"></i>
                        Download
                      </button>

                      {document.verificationStatus === 'PENDING' && (
                        <>
                          <button className="btn btn-sm btn-success" onClick={() => handleVerifyDocument(document.id, 'VERIFIED')}>
                            <i className="fas fa-check me-1"></i>
                            Verify & Send Email
                          </button>
                          <button className="btn btn-sm btn-danger" onClick={() => handleVerifyDocument(document.id, 'REJECTED')}>
                            <i className="fas fa-times me-1"></i>
                            Reject & Send Email
                          </button>
                        </>
                      )}

                      {document.verificationStatus !== 'PENDING' && (
                        <button className="btn btn-sm btn-warning" onClick={() => handleVerifyDocument(document.id, 'PENDING')}>
                          <i className="fas fa-undo me-1"></i>
                          Reset
                        </button>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Comment Modal */}
      {showCommentModal && (
        <div className="modal fade show d-block" tabIndex={-1} style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title">
                  <i className="fas fa-comment me-2"></i>
                  {actionType === 'REJECTED' ? 'Rejection Comment' : 'Verification Comment'}
                </h5>
                <button type="button" className="btn-close" onClick={handleModalCancel}></button>
              </div>
              <div className="modal-body">
                <div className="mb-3">
                  <p className="text-muted">
                    Document:{' '}
                    <strong>
                      {selectedDocument?.type} - {selectedDocument?.fileName}
                    </strong>
                  </p>
                  <p className="text-muted">
                    Doctor:{' '}
                    <strong>
                      {selectedDocument?.doctor.internalUser?.firstName} {selectedDocument?.doctor.internalUser?.lastName}
                    </strong>
                  </p>
                </div>
                <div className="mb-3">
                  <label htmlFor="comment" className="form-label">
                    {actionType === 'REJECTED' ? 'Reason for rejection (required):' : 'Comment (optional):'}
                  </label>
                  <textarea
                    id="comment"
                    className="form-control"
                    rows={4}
                    value={comment}
                    onChange={e => setComment(e.target.value)}
                    placeholder={
                      actionType === 'REJECTED'
                        ? 'Please provide a reason for rejecting this document...'
                        : 'Add any additional comments...'
                    }
                    required={actionType === 'REJECTED'}
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={handleModalCancel}>
                  <i className="fas fa-times me-1"></i>
                  Cancel
                </button>
                <button
                  type="button"
                  className={`btn ${actionType === 'REJECTED' ? 'btn-danger' : 'btn-success'}`}
                  onClick={handleModalSubmit}
                  disabled={actionType === 'REJECTED' && !comment.trim()}
                >
                  <i className={`fas ${actionType === 'REJECTED' ? 'fa-times' : 'fa-check'} me-1`}></i>
                  {actionType === 'REJECTED' ? 'Reject & Send Email' : 'Verify & Send Email'}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminDashboard;
