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

describe('TypePlante e2e test', () => {
  const typePlantePageUrl = '/type-plante';
  const typePlantePageUrlPattern = new RegExp('/type-plante(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const typePlanteSample = { name: 'along' };

  let typePlante;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/type-plantes+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/type-plantes').as('postEntityRequest');
    cy.intercept('DELETE', '/api/type-plantes/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (typePlante) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/type-plantes/${typePlante.id}`,
      }).then(() => {
        typePlante = undefined;
      });
    }
  });

  it('TypePlantes menu should load TypePlantes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('type-plante');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TypePlante').should('exist');
    cy.url().should('match', typePlantePageUrlPattern);
  });

  describe('TypePlante page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(typePlantePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TypePlante page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/type-plante/new$'));
        cy.getEntityCreateUpdateHeading('TypePlante');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', typePlantePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/type-plantes',
          body: typePlanteSample,
        }).then(({ body }) => {
          typePlante = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/type-plantes+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [typePlante],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(typePlantePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TypePlante page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('typePlante');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', typePlantePageUrlPattern);
      });

      it('edit button click should load edit TypePlante page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TypePlante');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', typePlantePageUrlPattern);
      });

      it('edit button click should load edit TypePlante page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TypePlante');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', typePlantePageUrlPattern);
      });

      it('last delete button click should delete instance of TypePlante', () => {
        cy.intercept('GET', '/api/type-plantes/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('typePlante').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', typePlantePageUrlPattern);

        typePlante = undefined;
      });
    });
  });

  describe('new TypePlante page', () => {
    beforeEach(() => {
      cy.visit(`${typePlantePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TypePlante');
    });

    it('should create an instance of TypePlante', () => {
      cy.get(`[data-cy="name"]`).type('steam toward');
      cy.get(`[data-cy="name"]`).should('have.value', 'steam toward');

      cy.get(`[data-cy="humiditeMax"]`).type('16607');
      cy.get(`[data-cy="humiditeMax"]`).should('have.value', '16607');

      cy.get(`[data-cy="humiditeMin"]`).type('9000');
      cy.get(`[data-cy="humiditeMin"]`).should('have.value', '9000');

      cy.get(`[data-cy="temperature"]`).type('31680');
      cy.get(`[data-cy="temperature"]`).should('have.value', '31680');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        typePlante = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', typePlantePageUrlPattern);
    });
  });
});
