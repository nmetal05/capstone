import './footer.scss';
import React from 'react';
import { Col, Row } from 'reactstrap';

const Footer = () => (
  <footer className="footer-container">
    <div className="footer-content">
      <Row className="align-items-center">
        <Col md="6" className="text-center text-md-start">
          <p className="copyright-text">&copy; {new Date().getFullYear()} AlloMed. All rights reserved.</p>
        </Col>
        <Col md="6" className="text-center text-md-end">
          <div className="footer-links">
            <a href="/privacy" className="footer-link">
              Privacy Policy
            </a>
            <span className="link-separator">|</span>
            <a href="/terms" className="footer-link">
              Terms of Service
            </a>
            <span className="link-separator">|</span>
            <a href="/contact" className="footer-link">
              Contact Us
            </a>
          </div>
        </Col>
      </Row>
    </div>
  </footer>
);

export default Footer;
