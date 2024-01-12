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

describe('Ferme e2e test', () => {
  const fermePageUrl = '/ferme';
  const fermePageUrlPattern = new RegExp('/ferme(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const fermeSample = { libelle: 'remount peer process' };

  let ferme;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/fermes+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/fermes').as('postEntityRequest');
    cy.intercept('DELETE', '/api/fermes/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (ferme) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/fermes/${ferme.id}`,
      }).then(() => {
        ferme = undefined;
      });
    }
  });

  it('Fermes menu should load Fermes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('ferme');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Ferme').should('exist');
    cy.url().should('match', fermePageUrlPattern);
  });

  describe('Ferme page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(fermePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Ferme page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/ferme/new$'));
        cy.getEntityCreateUpdateHeading('Ferme');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', fermePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/fermes',
          body: fermeSample,
        }).then(({ body }) => {
          ferme = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/fermes+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [ferme],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(fermePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Ferme page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('ferme');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', fermePageUrlPattern);
      });

      it('edit button click should load edit Ferme page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Ferme');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', fermePageUrlPattern);
      });

      it('edit button click should load edit Ferme page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Ferme');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', fermePageUrlPattern);
      });

      it('last delete button click should delete instance of Ferme', () => {
        cy.intercept('GET', '/api/fermes/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('ferme').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', fermePageUrlPattern);

        ferme = undefined;
      });
    });
  });

  describe('new Ferme page', () => {
    beforeEach(() => {
      cy.visit(`${fermePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Ferme');
    });

    it('should create an instance of Ferme', () => {
      cy.get(`[data-cy="libelle"]`).type('jagged especially');
      cy.get(`[data-cy="libelle"]`).should('have.value', 'jagged especially');

      cy.get(`[data-cy="photo"]`).type('rasterize till until');
      cy.get(`[data-cy="photo"]`).should('have.value', 'rasterize till until');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        ferme = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', fermePageUrlPattern);
    });
  });
});
