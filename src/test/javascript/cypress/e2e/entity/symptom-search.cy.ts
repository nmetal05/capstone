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

describe('SymptomSearch e2e test', () => {
  const symptomSearchPageUrl = '/symptom-search';
  const symptomSearchPageUrlPattern = new RegExp('/symptom-search(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const symptomSearchSample = {
    searchDate: '2025-04-26T04:26:57.815Z',
    symptoms: 'even snack',
    aiResponseJson: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
  };

  let symptomSearch;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/symptom-searches+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/symptom-searches').as('postEntityRequest');
    cy.intercept('DELETE', '/api/symptom-searches/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (symptomSearch) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/symptom-searches/${symptomSearch.id}`,
      }).then(() => {
        symptomSearch = undefined;
      });
    }
  });

  it('SymptomSearches menu should load SymptomSearches page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('symptom-search');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('SymptomSearch').should('exist');
    cy.url().should('match', symptomSearchPageUrlPattern);
  });

  describe('SymptomSearch page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(symptomSearchPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create SymptomSearch page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/symptom-search/new$'));
        cy.getEntityCreateUpdateHeading('SymptomSearch');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', symptomSearchPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/symptom-searches',
          body: symptomSearchSample,
        }).then(({ body }) => {
          symptomSearch = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/symptom-searches+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/symptom-searches?page=0&size=20>; rel="last",<http://localhost/api/symptom-searches?page=0&size=20>; rel="first"',
              },
              body: [symptomSearch],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(symptomSearchPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details SymptomSearch page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('symptomSearch');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', symptomSearchPageUrlPattern);
      });

      it('edit button click should load edit SymptomSearch page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SymptomSearch');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', symptomSearchPageUrlPattern);
      });

      it('edit button click should load edit SymptomSearch page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SymptomSearch');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', symptomSearchPageUrlPattern);
      });

      it('last delete button click should delete instance of SymptomSearch', () => {
        cy.intercept('GET', '/api/symptom-searches/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('symptomSearch').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', symptomSearchPageUrlPattern);

        symptomSearch = undefined;
      });
    });
  });

  describe('new SymptomSearch page', () => {
    beforeEach(() => {
      cy.visit(`${symptomSearchPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('SymptomSearch');
    });

    it('should create an instance of SymptomSearch', () => {
      cy.get(`[data-cy="searchDate"]`).type('2025-04-25T23:58');
      cy.get(`[data-cy="searchDate"]`).blur();
      cy.get(`[data-cy="searchDate"]`).should('have.value', '2025-04-25T23:58');

      cy.get(`[data-cy="symptoms"]`).type('sonnet upward');
      cy.get(`[data-cy="symptoms"]`).should('have.value', 'sonnet upward');

      cy.get(`[data-cy="aiResponseJson"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="aiResponseJson"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        symptomSearch = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', symptomSearchPageUrlPattern);
    });
  });
});
