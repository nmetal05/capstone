import React from 'react';
import { Alert } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { useNavigate, useLocation } from 'react-router-dom';
import { getLoginUrl } from 'app/shared/util/url-utils';
import './styles/auth-card.scss';

interface AuthCardProps {
  account: any;
}

const AuthCard: React.FC<AuthCardProps> = ({ account }) => {
  const navigate = useNavigate();
  const pageLocation = useLocation();

  if (account?.login) {
    return (
      <div className="auth-card logged-in">
        <Alert color="success" className="rounded-lg shadow-sm">
          <div className="d-flex align-items-center">
            <i className="fas fa-check-circle me-3"></i>
            <Translate contentKey="home.logged.message" interpolate={{ username: account.login }}>
              You are logged in as user {account.login}.
            </Translate>
          </div>
        </Alert>
      </div>
    );
  }

  return (
    <div className="auth-card">
      <Alert color="light" className="rounded-lg shadow-sm">
        <div className="d-flex align-items-start">
          <i className="fas fa-info-circle me-3 mt-1"></i>
          <div>
            <Translate contentKey="global.messages.info.authenticated.prefix">If you want to </Translate>
            <a
              className="auth-link"
              onClick={() =>
                navigate(getLoginUrl(), {
                  state: { from: pageLocation },
                })
              }
            >
              <Translate contentKey="global.messages.info.authenticated.link">sign in</Translate>
            </a>
            <Translate contentKey="global.messages.info.authenticated.suffix">
              , you can try the default accounts:
              <br />- Administrator (login=&quot;admin&quot; and password=&quot;admin&quot;)
              <br />- User (login=&quot;user&quot; and password=&quot;user&quot;).
            </Translate>
          </div>
        </div>
      </Alert>
    </div>
  );
};

export default AuthCard;
