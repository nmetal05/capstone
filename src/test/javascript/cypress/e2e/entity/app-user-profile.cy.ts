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

describe('AppUserProfile e2e test', () => {
  const appUserProfilePageUrl = '/app-user-profile';
  const appUserProfilePageUrlPattern = new RegExp('/app-user-profile(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const appUserProfileSample = {};

  let appUserProfile;
  // let user;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/users',
      body: {"id":"652a43ae-b76b-4a52-8488-a1da63ede7e7","login":"Zt&@XEwZ","firstName":"April","lastName":"Stoltenberg","email":"Emelia_Morissette@gmail.com","imageUrl":"comparison","langKey":"yuppify"},
    }).then(({ body }) => {
      user = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/app-user-profiles+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/app-user-profiles').as('postEntityRequest');
    cy.intercept('DELETE', '/api/app-user-profiles/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [user],
    });

  });
   */

  afterEach(() => {
    if (appUserProfile) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/app-user-profiles/${appUserProfile.id}`,
      }).then(() => {
        appUserProfile = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (user) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/users/${user.id}`,
      }).then(() => {
        user = undefined;
      });
    }
  });
   */

  it('AppUserProfiles menu should load AppUserProfiles page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('app-user-profile');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('AppUserProfile').should('exist');
    cy.url().should('match', appUserProfilePageUrlPattern);
  });

  describe('AppUserProfile page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(appUserProfilePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create AppUserProfile page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/app-user-profile/new$'));
        cy.getEntityCreateUpdateHeading('AppUserProfile');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', appUserProfilePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/app-user-profiles',
          body: {
            ...appUserProfileSample,
            internalUser: user,
          },
        }).then(({ body }) => {
          appUserProfile = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/app-user-profiles+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/app-user-profiles?page=0&size=20>; rel="last",<http://localhost/api/app-user-profiles?page=0&size=20>; rel="first"',
              },
              body: [appUserProfile],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(appUserProfilePageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(appUserProfilePageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details AppUserProfile page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('appUserProfile');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', appUserProfilePageUrlPattern);
      });

      it('edit button click should load edit AppUserProfile page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AppUserProfile');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', appUserProfilePageUrlPattern);
      });

      it('edit button click should load edit AppUserProfile page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AppUserProfile');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', appUserProfilePageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of AppUserProfile', () => {
        cy.intercept('GET', '/api/app-user-profiles/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('appUserProfile').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', appUserProfilePageUrlPattern);

        appUserProfile = undefined;
      });
    });
  });

  describe('new AppUserProfile page', () => {
    beforeEach(() => {
      cy.visit(`${appUserProfilePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('AppUserProfile');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of AppUserProfile', () => {
      cy.get(`[data-cy="latitude"]`).type('28042.43');
      cy.get(`[data-cy="latitude"]`).should('have.value', '28042.43');

      cy.get(`[data-cy="longitude"]`).type('25555.4');
      cy.get(`[data-cy="longitude"]`).should('have.value', '25555.4');

      cy.get(`[data-cy="lastLoginIp"]`).type('drat');
      cy.get(`[data-cy="lastLoginIp"]`).should('have.value', 'drat');

      cy.get(`[data-cy="lastUserAgent"]`).type('worth shakily');
      cy.get(`[data-cy="lastUserAgent"]`).should('have.value', 'worth shakily');

      cy.get(`[data-cy="lastLoginDate"]`).type('2025-04-26T03:20');
      cy.get(`[data-cy="lastLoginDate"]`).blur();
      cy.get(`[data-cy="lastLoginDate"]`).should('have.value', '2025-04-26T03:20');

      cy.get(`[data-cy="internalUser"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        appUserProfile = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', appUserProfilePageUrlPattern);
    });
  });
});
