import React from 'react';
import './styles/doctor-dashboard.scss';

const DoctorDashboard: React.FC = () => {
  return (
    <div className="card shadow-sm h-100">
      <div className="card-body">
        <h3 className="card-title mb-4">
          <i className="fas fa-user-md me-2"></i>
          Doctor Dashboard
        </h3>
        <p className="text-muted mb-4">Welcome to your doctor dashboard. Here you can manage your appointments and patient information.</p>

        <div className="dashboard-stats">
          <div className="row">
            <div className="col-md-4 mb-4">
              <div className="stat-card">
                <div className="stat-icon">
                  <i className="fas fa-calendar-check"></i>
                </div>
                <div className="stat-info">
                  <h4>Appointments</h4>
                  <p className="stat-value">0</p>
                  <p className="stat-label">Today&apos;s appointments</p>
                </div>
              </div>
            </div>
            <div className="col-md-4 mb-4">
              <div className="stat-card">
                <div className="stat-icon">
                  <i className="fas fa-users"></i>
                </div>
                <div className="stat-info">
                  <h4>Patients</h4>
                  <p className="stat-value">0</p>
                  <p className="stat-label">Total patients</p>
                </div>
              </div>
            </div>
            <div className="col-md-4 mb-4">
              <div className="stat-card">
                <div className="stat-icon">
                  <i className="fas fa-star"></i>
                </div>
                <div className="stat-info">
                  <h4>Rating</h4>
                  <p className="stat-value">0.0</p>
                  <p className="stat-label">Average rating</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="dashboard-actions mt-4">
          <div className="row">
            <div className="col-md-6 mb-4">
              <div className="action-card">
                <h5>
                  <i className="fas fa-calendar-plus me-2"></i>
                  Schedule Management
                </h5>
                <p>Manage your availability and appointment schedule</p>
                <button className="btn btn-primary w-100">
                  <i className="fas fa-cog me-2"></i>
                  Manage Schedule
                </button>
              </div>
            </div>
            <div className="col-md-6 mb-4">
              <div className="action-card">
                <h5>
                  <i className="fas fa-file-medical me-2"></i>
                  Patient Records
                </h5>
                <p>Access and manage your patient medical records</p>
                <button className="btn btn-primary w-100">
                  <i className="fas fa-folder-open me-2"></i>
                  View Records
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DoctorDashboard;
