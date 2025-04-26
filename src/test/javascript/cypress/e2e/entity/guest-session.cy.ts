import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('GuestSession e2e test', () => {
  const guestSessionPageUrl = '/guest-session';
  const guestSessionPageUrlPattern = new RegExp('/guest-session(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const guestSessionSample = {
    sessionId: 'until hopelessly gee',
    createdAt: '2025-04-26T01:31:52.135Z',
    lastActiveAt: '2025-04-26T04:07:58.433Z',
    ipAddress: 'whose yowza',
    userAgent: 'hoof yet',
  };

  let guestSession;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/guest-sessions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/guest-sessions').as('postEntityRequest');
    cy.intercept('DELETE', '/api/guest-sessions/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (guestSession) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/guest-sessions/${guestSession.id}`,
      }).then(() => {
        guestSession = undefined;
      });
    }
  });

  it('GuestSessions menu should load GuestSessions page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('guest-session');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('GuestSession').should('exist');
    cy.url().should('match', guestSessionPageUrlPattern);
  });

  describe('GuestSession page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(guestSessionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create GuestSession page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/guest-session/new$'));
        cy.getEntityCreateUpdateHeading('GuestSession');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', guestSessionPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/guest-sessions',
          body: guestSessionSample,
        }).then(({ body }) => {
          guestSession = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/guest-sessions+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/guest-sessions?page=0&size=20>; rel="last",<http://localhost/api/guest-sessions?page=0&size=20>; rel="first"',
              },
              body: [guestSession],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(guestSessionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details GuestSession page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('guestSession');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', guestSessionPageUrlPattern);
      });

      it('edit button click should load edit GuestSession page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('GuestSession');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', guestSessionPageUrlPattern);
      });

      it('edit button click should load edit GuestSession page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('GuestSession');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', guestSessionPageUrlPattern);
      });

      it('last delete button click should delete instance of GuestSession', () => {
        cy.intercept('GET', '/api/guest-sessions/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('guestSession').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', guestSessionPageUrlPattern);

        guestSession = undefined;
      });
    });
  });

  describe('new GuestSession page', () => {
    beforeEach(() => {
      cy.visit(`${guestSessionPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('GuestSession');
    });

    it('should create an instance of GuestSession', () => {
      cy.get(`[data-cy="sessionId"]`).type('hydrant');
      cy.get(`[data-cy="sessionId"]`).should('have.value', 'hydrant');

      cy.get(`[data-cy="createdAt"]`).type('2025-04-25T18:28');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2025-04-25T18:28');

      cy.get(`[data-cy="lastActiveAt"]`).type('2025-04-25T14:32');
      cy.get(`[data-cy="lastActiveAt"]`).blur();
      cy.get(`[data-cy="lastActiveAt"]`).should('have.value', '2025-04-25T14:32');

      cy.get(`[data-cy="ipAddress"]`).type('silver');
      cy.get(`[data-cy="ipAddress"]`).should('have.value', 'silver');

      cy.get(`[data-cy="userAgent"]`).type('downshift');
      cy.get(`[data-cy="userAgent"]`).should('have.value', 'downshift');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        guestSession = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', guestSessionPageUrlPattern);
    });
  });
});
