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

describe('DoctorDocument e2e test', () => {
  const doctorDocumentPageUrl = '/doctor-document';
  const doctorDocumentPageUrlPattern = new RegExp('/doctor-document(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const doctorDocumentSample = {"type":"OTHER","fileName":"lest","fileContent":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci5wbmc=","fileContentContentType":"unknown","uploadDate":"2025-04-26T12:51:25.021Z","verificationStatus":"PENDING"};

  let doctorDocument;
  // let doctorProfile;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/doctor-profiles',
      body: {"phoneNumber":"govern","officeAddress":"westernise","latitude":32084.79,"longitude":24436.5,"inpeCode":"emerge forecast regarding","isVerified":false,"lastLoginIp":"er microchip","lastUserAgent":"freight huzzah","lastLoginDate":"2025-04-25T17:22:26.510Z"},
    }).then(({ body }) => {
      doctorProfile = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/doctor-documents+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/doctor-documents').as('postEntityRequest');
    cy.intercept('DELETE', '/api/doctor-documents/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/doctor-profiles', {
      statusCode: 200,
      body: [doctorProfile],
    });

  });
   */

  afterEach(() => {
    if (doctorDocument) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/doctor-documents/${doctorDocument.id}`,
      }).then(() => {
        doctorDocument = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
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
   */

  it('DoctorDocuments menu should load DoctorDocuments page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('doctor-document');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('DoctorDocument').should('exist');
    cy.url().should('match', doctorDocumentPageUrlPattern);
  });

  describe('DoctorDocument page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(doctorDocumentPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create DoctorDocument page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/doctor-document/new$'));
        cy.getEntityCreateUpdateHeading('DoctorDocument');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', doctorDocumentPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/doctor-documents',
          body: {
            ...doctorDocumentSample,
            doctor: doctorProfile,
          },
        }).then(({ body }) => {
          doctorDocument = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/doctor-documents+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/doctor-documents?page=0&size=20>; rel="last",<http://localhost/api/doctor-documents?page=0&size=20>; rel="first"',
              },
              body: [doctorDocument],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(doctorDocumentPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(doctorDocumentPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details DoctorDocument page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('doctorDocument');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', doctorDocumentPageUrlPattern);
      });

      it('edit button click should load edit DoctorDocument page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('DoctorDocument');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', doctorDocumentPageUrlPattern);
      });

      it('edit button click should load edit DoctorDocument page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('DoctorDocument');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', doctorDocumentPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of DoctorDocument', () => {
        cy.intercept('GET', '/api/doctor-documents/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('doctorDocument').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', doctorDocumentPageUrlPattern);

        doctorDocument = undefined;
      });
    });
  });

  describe('new DoctorDocument page', () => {
    beforeEach(() => {
      cy.visit(`${doctorDocumentPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('DoctorDocument');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of DoctorDocument', () => {
      cy.get(`[data-cy="type"]`).select('LICENSE');

      cy.get(`[data-cy="fileName"]`).type('difficult');
      cy.get(`[data-cy="fileName"]`).should('have.value', 'difficult');

      cy.setFieldImageAsBytesOfEntity('fileContent', 'integration-test.png', 'image/png');

      cy.get(`[data-cy="uploadDate"]`).type('2025-04-26T04:40');
      cy.get(`[data-cy="uploadDate"]`).blur();
      cy.get(`[data-cy="uploadDate"]`).should('have.value', '2025-04-26T04:40');

      cy.get(`[data-cy="verificationStatus"]`).select('PENDING');

      cy.get(`[data-cy="doctor"]`).select(1);

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        doctorDocument = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', doctorDocumentPageUrlPattern);
    });
  });
});
