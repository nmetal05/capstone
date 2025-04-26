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

describe('DoctorViewHistory e2e test', () => {
  const doctorViewHistoryPageUrl = '/doctor-view-history';
  const doctorViewHistoryPageUrlPattern = new RegExp('/doctor-view-history(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const doctorViewHistorySample = {"viewDate":"2025-04-26T05:58:20.266Z"};

  let doctorViewHistory;
  // let appUserProfile;
  // let doctorProfile;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/app-user-profiles',
      body: {"latitude":6232.15,"longitude":1160.78,"lastLoginIp":"boo doting","lastUserAgent":"yowza helpless","lastLoginDate":"2025-04-26T13:41:28.551Z"},
    }).then(({ body }) => {
      appUserProfile = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/doctor-profiles',
      body: {"phoneNumber":"contrail pfft","officeAddress":"on afore","latitude":16086.94,"longitude":30016.87,"inpeCode":"vacantly","isVerified":false,"lastLoginIp":"narrowcast sedately nor","lastUserAgent":"terraform","lastLoginDate":"2025-04-25T22:26:27.157Z"},
    }).then(({ body }) => {
      doctorProfile = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/doctor-view-histories+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/doctor-view-histories').as('postEntityRequest');
    cy.intercept('DELETE', '/api/doctor-view-histories/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/app-user-profiles', {
      statusCode: 200,
      body: [appUserProfile],
    });

    cy.intercept('GET', '/api/doctor-profiles', {
      statusCode: 200,
      body: [doctorProfile],
    });

  });
   */

  afterEach(() => {
    if (doctorViewHistory) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/doctor-view-histories/${doctorViewHistory.id}`,
      }).then(() => {
        doctorViewHistory = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (appUserProfile) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/app-user-profiles/${appUserProfile.id}`,
      }).then(() => {
        appUserProfile = undefined;
      });
    }
    if (doctorProfile) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/doctor-profiles/${doctorProfile.id}`,
      }).then(() => {
        doctorProfile = undefined;
      });
    }
  });
   */

  it('DoctorViewHistories menu should load DoctorViewHistories page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('doctor-view-history');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('DoctorViewHistory').should('exist');
    cy.url().should('match', doctorViewHistoryPageUrlPattern);
  });

  describe('DoctorViewHistory page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(doctorViewHistoryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create DoctorViewHistory page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/doctor-view-history/new$'));
        cy.getEntityCreateUpdateHeading('DoctorViewHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', doctorViewHistoryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/doctor-view-histories',
          body: {
            ...doctorViewHistorySample,
            user: appUserProfile,
            doctor: doctorProfile,
          },
        }).then(({ body }) => {
          doctorViewHistory = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/doctor-view-histories+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/doctor-view-histories?page=0&size=20>; rel="last",<http://localhost/api/doctor-view-histories?page=0&size=20>; rel="first"',
              },
              body: [doctorViewHistory],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(doctorViewHistoryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(doctorViewHistoryPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details DoctorViewHistory page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('doctorViewHistory');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', doctorViewHistoryPageUrlPattern);
      });

      it('edit button click should load edit DoctorViewHistory page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('DoctorViewHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', doctorViewHistoryPageUrlPattern);
      });

      it('edit button click should load edit DoctorViewHistory page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('DoctorViewHistory');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', doctorViewHistoryPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of DoctorViewHistory', () => {
        cy.intercept('GET', '/api/doctor-view-histories/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('doctorViewHistory').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', doctorViewHistoryPageUrlPattern);

        doctorViewHistory = undefined;
      });
    });
  });

  describe('new DoctorViewHistory page', () => {
    beforeEach(() => {
      cy.visit(`${doctorViewHistoryPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('DoctorViewHistory');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of DoctorViewHistory', () => {
      cy.get(`[data-cy="viewDate"]`).type('2025-04-25T17:34');
      cy.get(`[data-cy="viewDate"]`).blur();
      cy.get(`[data-cy="viewDate"]`).should('have.value', '2025-04-25T17:34');

      cy.get(`[data-cy="user"]`).select(1);
      cy.get(`[data-cy="doctor"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        doctorViewHistory = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', doctorViewHistoryPageUrlPattern);
    });
  });
});
