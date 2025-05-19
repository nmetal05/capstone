import React from 'react';
import { Translate } from 'react-jhipster';
import './styles/hero-section.scss';

const HeroSection: React.FC = () => {
  return (
    <div className="hero-section">
      <h1 className="display-4 gradient-text">
        <Translate contentKey="home.title">Welcome, Java Hipster!</Translate>
      </h1>
    </div>
  );
};

export default HeroSection;
