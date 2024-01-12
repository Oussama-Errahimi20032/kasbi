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

describe('Parcelle e2e test', () => {
  const parcellePageUrl = '/parcelle';
  const parcellePageUrlPattern = new RegExp('/parcelle(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const parcelleSample = { libelle: 'never twister where' };

  let parcelle;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/parcelles+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/parcelles').as('postEntityRequest');
    cy.intercept('DELETE', '/api/parcelles/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (parcelle) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/parcelles/${parcelle.id}`,
      }).then(() => {
        parcelle = undefined;
      });
    }
  });

  it('Parcelles menu should load Parcelles page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('parcelle');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Parcelle').should('exist');
    cy.url().should('match', parcellePageUrlPattern);
  });

  describe('Parcelle page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(parcellePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Parcelle page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/parcelle/new$'));
        cy.getEntityCreateUpdateHeading('Parcelle');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', parcellePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/parcelles',
          body: parcelleSample,
        }).then(({ body }) => {
          parcelle = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/parcelles+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [parcelle],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(parcellePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Parcelle page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('parcelle');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', parcellePageUrlPattern);
      });

      it('edit button click should load edit Parcelle page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Parcelle');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', parcellePageUrlPattern);
      });

      it('edit button click should load edit Parcelle page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Parcelle');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', parcellePageUrlPattern);
      });

      it('last delete button click should delete instance of Parcelle', () => {
        cy.intercept('GET', '/api/parcelles/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('parcelle').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', parcellePageUrlPattern);

        parcelle = undefined;
      });
    });
  });

  describe('new Parcelle page', () => {
    beforeEach(() => {
      cy.visit(`${parcellePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Parcelle');
    });

    it('should create an instance of Parcelle', () => {
      cy.get(`[data-cy="libelle"]`).type('excluding');
      cy.get(`[data-cy="libelle"]`).should('have.value', 'excluding');

      cy.get(`[data-cy="photo"]`).type('closely among');
      cy.get(`[data-cy="photo"]`).should('have.value', 'closely among');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        parcelle = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', parcellePageUrlPattern);
    });
  });
});
