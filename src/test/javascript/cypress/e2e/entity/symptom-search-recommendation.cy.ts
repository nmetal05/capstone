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

describe('SymptomSearchRecommendation e2e test', () => {
  const symptomSearchRecommendationPageUrl = '/symptom-search-recommendation';
  const symptomSearchRecommendationPageUrlPattern = new RegExp('/symptom-search-recommendation(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const symptomSearchRecommendationSample = { confidenceScore: 20233.21, rank: 15555, reasoning: 'aha internalize' };

  let symptomSearchRecommendation;
  let symptomSearch;
  let specialization;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/symptom-searches',
      body: {
        searchDate: '2025-04-25T15:44:28.217Z',
        symptoms: 'uh-huh and neaten',
        aiResponseJson: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
      },
    }).then(({ body }) => {
      symptomSearch = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/specializations',
      body: { name: 'cram after', description: 'accessorise gently amount' },
    }).then(({ body }) => {
      specialization = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/symptom-search-recommendations+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/symptom-search-recommendations').as('postEntityRequest');
    cy.intercept('DELETE', '/api/symptom-search-recommendations/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/symptom-searches', {
      statusCode: 200,
      body: [symptomSearch],
    });

    cy.intercept('GET', '/api/specializations', {
      statusCode: 200,
      body: [specialization],
    });
  });

  afterEach(() => {
    if (symptomSearchRecommendation) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/symptom-search-recommendations/${symptomSearchRecommendation.id}`,
      }).then(() => {
        symptomSearchRecommendation = undefined;
      });
    }
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
    if (specialization) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/specializations/${specialization.id}`,
      }).then(() => {
        specialization = undefined;
      });
    }
  });

  it('SymptomSearchRecommendations menu should load SymptomSearchRecommendations page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('symptom-search-recommendation');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('SymptomSearchRecommendation').should('exist');
    cy.url().should('match', symptomSearchRecommendationPageUrlPattern);
  });

  describe('SymptomSearchRecommendation page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(symptomSearchRecommendationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create SymptomSearchRecommendation page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/symptom-search-recommendation/new$'));
        cy.getEntityCreateUpdateHeading('SymptomSearchRecommendation');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', symptomSearchRecommendationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/symptom-search-recommendations',
          body: {
            ...symptomSearchRecommendationSample,
            search: symptomSearch,
            specialization,
          },
        }).then(({ body }) => {
          symptomSearchRecommendation = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/symptom-search-recommendations+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/symptom-search-recommendations?page=0&size=20>; rel="last",<http://localhost/api/symptom-search-recommendations?page=0&size=20>; rel="first"',
              },
              body: [symptomSearchRecommendation],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(symptomSearchRecommendationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details SymptomSearchRecommendation page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('symptomSearchRecommendation');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', symptomSearchRecommendationPageUrlPattern);
      });

      it('edit button click should load edit SymptomSearchRecommendation page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SymptomSearchRecommendation');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', symptomSearchRecommendationPageUrlPattern);
      });

      it('edit button click should load edit SymptomSearchRecommendation page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SymptomSearchRecommendation');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', symptomSearchRecommendationPageUrlPattern);
      });

      it('last delete button click should delete instance of SymptomSearchRecommendation', () => {
        cy.intercept('GET', '/api/symptom-search-recommendations/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('symptomSearchRecommendation').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', symptomSearchRecommendationPageUrlPattern);

        symptomSearchRecommendation = undefined;
      });
    });
  });

  describe('new SymptomSearchRecommendation page', () => {
    beforeEach(() => {
      cy.visit(`${symptomSearchRecommendationPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('SymptomSearchRecommendation');
    });

    it('should create an instance of SymptomSearchRecommendation', () => {
      cy.get(`[data-cy="confidenceScore"]`).type('19629.54');
      cy.get(`[data-cy="confidenceScore"]`).should('have.value', '19629.54');

      cy.get(`[data-cy="rank"]`).type('10696');
      cy.get(`[data-cy="rank"]`).should('have.value', '10696');

      cy.get(`[data-cy="reasoning"]`).type('pish reckless or');
      cy.get(`[data-cy="reasoning"]`).should('have.value', 'pish reckless or');

      cy.get(`[data-cy="search"]`).select(1);
      cy.get(`[data-cy="specialization"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        symptomSearchRecommendation = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', symptomSearchRecommendationPageUrlPattern);
    });
  });
});
