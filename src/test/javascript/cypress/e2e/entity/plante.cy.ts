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

describe('Plante e2e test', () => {
  const plantePageUrl = '/plante';
  const plantePageUrlPattern = new RegExp('/plante(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const planteSample = { libelle: 'immediately moisturise' };

  let plante;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/plantes+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/plantes').as('postEntityRequest');
    cy.intercept('DELETE', '/api/plantes/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (plante) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/plantes/${plante.id}`,
      }).then(() => {
        plante = undefined;
      });
    }
  });

  it('Plantes menu should load Plantes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('plante');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Plante').should('exist');
    cy.url().should('match', plantePageUrlPattern);
  });

  describe('Plante page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(plantePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Plante page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/plante/new$'));
        cy.getEntityCreateUpdateHeading('Plante');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', plantePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/plantes',
          body: planteSample,
        }).then(({ body }) => {
          plante = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/plantes+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [plante],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(plantePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Plante page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('plante');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', plantePageUrlPattern);
      });

      it('edit button click should load edit Plante page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Plante');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', plantePageUrlPattern);
      });

      it('edit button click should load edit Plante page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Plante');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', plantePageUrlPattern);
      });

      it('last delete button click should delete instance of Plante', () => {
        cy.intercept('GET', '/api/plantes/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('plante').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', plantePageUrlPattern);

        plante = undefined;
      });
    });
  });

  describe('new Plante page', () => {
    beforeEach(() => {
      cy.visit(`${plantePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Plante');
    });

    it('should create an instance of Plante', () => {
      cy.get(`[data-cy="libelle"]`).type('square quince on');
      cy.get(`[data-cy="libelle"]`).should('have.value', 'square quince on');

      cy.get(`[data-cy="racine"]`).type('connote without midst');
      cy.get(`[data-cy="racine"]`).should('have.value', 'connote without midst');

      cy.get(`[data-cy="image"]`).type('galley why forestry');
      cy.get(`[data-cy="image"]`).should('have.value', 'galley why forestry');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        plante = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', plantePageUrlPattern);
    });
  });
});
