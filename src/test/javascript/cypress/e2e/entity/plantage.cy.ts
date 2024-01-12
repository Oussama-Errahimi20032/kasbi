import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Plantage e2e test', () => {
  const plantagePageUrl = '/plantage';
  const plantagePageUrlPattern = new RegExp('/plantage(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const plantageSample = { date: '2024-01-11' };

  let plantage;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/plantages+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/plantages').as('postEntityRequest');
    cy.intercept('DELETE', '/api/plantages/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (plantage) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/plantages/${plantage.id}`,
      }).then(() => {
        plantage = undefined;
      });
    }
  });

  it('Plantages menu should load Plantages page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('plantage');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Plantage').should('exist');
    cy.url().should('match', plantagePageUrlPattern);
  });

  describe('Plantage page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(plantagePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Plantage page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/plantage/new$'));
        cy.getEntityCreateUpdateHeading('Plantage');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', plantagePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/plantages',
          body: plantageSample,
        }).then(({ body }) => {
          plantage = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/plantages+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [plantage],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(plantagePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Plantage page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('plantage');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', plantagePageUrlPattern);
      });

      it('edit button click should load edit Plantage page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Plantage');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', plantagePageUrlPattern);
      });

      it('edit button click should load edit Plantage page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Plantage');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', plantagePageUrlPattern);
      });

      it('last delete button click should delete instance of Plantage', () => {
        cy.intercept('GET', '/api/plantages/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('plantage').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', plantagePageUrlPattern);

        plantage = undefined;
      });
    });
  });

  describe('new Plantage page', () => {
    beforeEach(() => {
      cy.visit(`${plantagePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Plantage');
    });

    it('should create an instance of Plantage', () => {
      cy.get(`[data-cy="date"]`).type('2024-01-11');
      cy.get(`[data-cy="date"]`).blur();
      cy.get(`[data-cy="date"]`).should('have.value', '2024-01-11');

      cy.get(`[data-cy="nombre"]`).type('15201');
      cy.get(`[data-cy="nombre"]`).should('have.value', '15201');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        plantage = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', plantagePageUrlPattern);
    });
  });
});
