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

describe('DoctorProfile e2e test', () => {
  const doctorProfilePageUrl = '/doctor-profile';
  const doctorProfilePageUrlPattern = new RegExp('/doctor-profile(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const doctorProfileSample = {"phoneNumber":"meanwhile mesh","officeAddress":"zowie thread wear","latitude":13777.38,"longitude":28792.94,"inpeCode":"correctly within","isVerified":false};

  let doctorProfile;
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
      body: {"id":"e12ddf19-76bd-4d0a-b509-557e4d271ef4","login":"kdZZD","firstName":"Calista","lastName":"Boehm","email":"Amira.Kilback20@hotmail.com","imageUrl":"retract","langKey":"besides"},
    }).then(({ body }) => {
      user = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/doctor-profiles+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/doctor-profiles').as('postEntityRequest');
    cy.intercept('DELETE', '/api/doctor-profiles/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [user],
    });

    cy.intercept('GET', '/api/specializations', {
      statusCode: 200,
      body: [],
    });

  });
   */

  afterEach(() => {
    if (doctorProfile) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/doctor-profiles/${doctorProfile.id}`,
      }).then(() => {
        doctorProfile = undefined;
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

  it('DoctorProfiles menu should load DoctorProfiles page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('doctor-profile');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('DoctorProfile').should('exist');
    cy.url().should('match', doctorProfilePageUrlPattern);
  });

  describe('DoctorProfile page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(doctorProfilePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create DoctorProfile page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/doctor-profile/new$'));
        cy.getEntityCreateUpdateHeading('DoctorProfile');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', doctorProfilePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/doctor-profiles',
          body: {
            ...doctorProfileSample,
            internalUser: user,
          },
        }).then(({ body }) => {
          doctorProfile = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/doctor-profiles+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/doctor-profiles?page=0&size=20>; rel="last",<http://localhost/api/doctor-profiles?page=0&size=20>; rel="first"',
              },
              body: [doctorProfile],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(doctorProfilePageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(doctorProfilePageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details DoctorProfile page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('doctorProfile');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', doctorProfilePageUrlPattern);
      });

      it('edit button click should load edit DoctorProfile page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('DoctorProfile');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', doctorProfilePageUrlPattern);
      });

      it('edit button click should load edit DoctorProfile page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('DoctorProfile');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', doctorProfilePageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of DoctorProfile', () => {
        cy.intercept('GET', '/api/doctor-profiles/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('doctorProfile').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', doctorProfilePageUrlPattern);

        doctorProfile = undefined;
      });
    });
  });

  describe('new DoctorProfile page', () => {
    beforeEach(() => {
      cy.visit(`${doctorProfilePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('DoctorProfile');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of DoctorProfile', () => {
      cy.get(`[data-cy="phoneNumber"]`).type('handover');
      cy.get(`[data-cy="phoneNumber"]`).should('have.value', 'handover');

      cy.get(`[data-cy="officeAddress"]`).type('nasalise');
      cy.get(`[data-cy="officeAddress"]`).should('have.value', 'nasalise');

      cy.get(`[data-cy="latitude"]`).type('26061.75');
      cy.get(`[data-cy="latitude"]`).should('have.value', '26061.75');

      cy.get(`[data-cy="longitude"]`).type('4303.32');
      cy.get(`[data-cy="longitude"]`).should('have.value', '4303.32');

      cy.get(`[data-cy="inpeCode"]`).type('scar noted assist');
      cy.get(`[data-cy="inpeCode"]`).should('have.value', 'scar noted assist');

      cy.get(`[data-cy="isVerified"]`).should('not.be.checked');
      cy.get(`[data-cy="isVerified"]`).click();
      cy.get(`[data-cy="isVerified"]`).should('be.checked');

      cy.get(`[data-cy="lastLoginIp"]`).type('pacemaker without bleakly');
      cy.get(`[data-cy="lastLoginIp"]`).should('have.value', 'pacemaker without bleakly');

      cy.get(`[data-cy="lastUserAgent"]`).type('ouch pupil');
      cy.get(`[data-cy="lastUserAgent"]`).should('have.value', 'ouch pupil');

      cy.get(`[data-cy="lastLoginDate"]`).type('2025-04-26T08:48');
      cy.get(`[data-cy="lastLoginDate"]`).blur();
      cy.get(`[data-cy="lastLoginDate"]`).should('have.value', '2025-04-26T08:48');

      cy.get(`[data-cy="internalUser"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        doctorProfile = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', doctorProfilePageUrlPattern);
    });
  });
});
