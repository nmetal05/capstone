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

describe('Specialization e2e test', () => {
  const specializationPageUrl = '/specialization';
  const specializationPageUrlPattern = new RegExp('/specialization(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const specializationSample = { name: 'frightened' };

  let specialization;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/specializations+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/specializations').as('postEntityRequest');
    cy.intercept('DELETE', '/api/specializations/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (specialization) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/specializations/${specialization.id}`,
      }).then(() => {
        specialization = undefined;
      });
    }
  });

  it('Specializations menu should load Specializations page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('specialization');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Specialization').should('exist');
    cy.url().should('match', specializationPageUrlPattern);
  });

  describe('Specialization page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(specializationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Specialization page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/specialization/new$'));
        cy.getEntityCreateUpdateHeading('Specialization');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', specializationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/specializations',
          body: specializationSample,
        }).then(({ body }) => {
          specialization = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/specializations+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/specializations?page=0&size=20>; rel="last",<http://localhost/api/specializations?page=0&size=20>; rel="first"',
              },
              body: [specialization],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(specializationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Specialization page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('specialization');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', specializationPageUrlPattern);
      });

      it('edit button click should load edit Specialization page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Specialization');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', specializationPageUrlPattern);
      });

      it('edit button click should load edit Specialization page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Specialization');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', specializationPageUrlPattern);
      });

      it('last delete button click should delete instance of Specialization', () => {
        cy.intercept('GET', '/api/specializations/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('specialization').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', specializationPageUrlPattern);

        specialization = undefined;
      });
    });
  });

  describe('new Specialization page', () => {
    beforeEach(() => {
      cy.visit(`${specializationPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Specialization');
    });

    it('should create an instance of Specialization', () => {
      cy.get(`[data-cy="name"]`).type('playfully bah ha');
      cy.get(`[data-cy="name"]`).should('have.value', 'playfully bah ha');

      cy.get(`[data-cy="description"]`).type('atop');
      cy.get(`[data-cy="description"]`).should('have.value', 'atop');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        specialization = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', specializationPageUrlPattern);
    });
  });
});
