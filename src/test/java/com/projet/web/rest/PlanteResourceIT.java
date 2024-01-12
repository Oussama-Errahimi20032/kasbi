package com.projet.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.projet.IntegrationTest;
import com.projet.domain.Plante;
import com.projet.repository.EntityManager;
import com.projet.repository.PlanteRepository;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link PlanteResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PlanteResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String DEFAULT_RACINE = "AAAAAAAAAA";
    private static final String UPDATED_RACINE = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/plantes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PlanteRepository planteRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Plante plante;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Plante createEntity(EntityManager em) {
        Plante plante = new Plante().libelle(DEFAULT_LIBELLE).racine(DEFAULT_RACINE).image(DEFAULT_IMAGE);
        return plante;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Plante createUpdatedEntity(EntityManager em) {
        Plante plante = new Plante().libelle(UPDATED_LIBELLE).racine(UPDATED_RACINE).image(UPDATED_IMAGE);
        return plante;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Plante.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        plante = createEntity(em);
    }

    @Test
    void createPlante() throws Exception {
        int databaseSizeBeforeCreate = planteRepository.findAll().collectList().block().size();
        // Create the Plante
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(plante))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Plante in the database
        List<Plante> planteList = planteRepository.findAll().collectList().block();
        assertThat(planteList).hasSize(databaseSizeBeforeCreate + 1);
        Plante testPlante = planteList.get(planteList.size() - 1);
        assertThat(testPlante.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
        assertThat(testPlante.getRacine()).isEqualTo(DEFAULT_RACINE);
        assertThat(testPlante.getImage()).isEqualTo(DEFAULT_IMAGE);
    }

    @Test
    void createPlanteWithExistingId() throws Exception {
        // Create the Plante with an existing ID
        plante.setId(1L);

        int databaseSizeBeforeCreate = planteRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(plante))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Plante in the database
        List<Plante> planteList = planteRepository.findAll().collectList().block();
        assertThat(planteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPlantesAsStream() {
        // Initialize the database
        planteRepository.save(plante).block();

        List<Plante> planteList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Plante.class)
            .getResponseBody()
            .filter(plante::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(planteList).isNotNull();
        assertThat(planteList).hasSize(1);
        Plante testPlante = planteList.get(0);
        assertThat(testPlante.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
        assertThat(testPlante.getRacine()).isEqualTo(DEFAULT_RACINE);
        assertThat(testPlante.getImage()).isEqualTo(DEFAULT_IMAGE);
    }

    @Test
    void getAllPlantes() {
        // Initialize the database
        planteRepository.save(plante).block();

        // Get all the planteList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(plante.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE))
            .jsonPath("$.[*].racine")
            .value(hasItem(DEFAULT_RACINE))
            .jsonPath("$.[*].image")
            .value(hasItem(DEFAULT_IMAGE));
    }

    @Test
    void getPlante() {
        // Initialize the database
        planteRepository.save(plante).block();

        // Get the plante
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, plante.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(plante.getId().intValue()))
            .jsonPath("$.libelle")
            .value(is(DEFAULT_LIBELLE))
            .jsonPath("$.racine")
            .value(is(DEFAULT_RACINE))
            .jsonPath("$.image")
            .value(is(DEFAULT_IMAGE));
    }

    @Test
    void getNonExistingPlante() {
        // Get the plante
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPlante() throws Exception {
        // Initialize the database
        planteRepository.save(plante).block();

        int databaseSizeBeforeUpdate = planteRepository.findAll().collectList().block().size();

        // Update the plante
        Plante updatedPlante = planteRepository.findById(plante.getId()).block();
        updatedPlante.libelle(UPDATED_LIBELLE).racine(UPDATED_RACINE).image(UPDATED_IMAGE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPlante.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPlante))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Plante in the database
        List<Plante> planteList = planteRepository.findAll().collectList().block();
        assertThat(planteList).hasSize(databaseSizeBeforeUpdate);
        Plante testPlante = planteList.get(planteList.size() - 1);
        assertThat(testPlante.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testPlante.getRacine()).isEqualTo(UPDATED_RACINE);
        assertThat(testPlante.getImage()).isEqualTo(UPDATED_IMAGE);
    }

    @Test
    void putNonExistingPlante() throws Exception {
        int databaseSizeBeforeUpdate = planteRepository.findAll().collectList().block().size();
        plante.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, plante.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(plante))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Plante in the database
        List<Plante> planteList = planteRepository.findAll().collectList().block();
        assertThat(planteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPlante() throws Exception {
        int databaseSizeBeforeUpdate = planteRepository.findAll().collectList().block().size();
        plante.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(plante))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Plante in the database
        List<Plante> planteList = planteRepository.findAll().collectList().block();
        assertThat(planteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPlante() throws Exception {
        int databaseSizeBeforeUpdate = planteRepository.findAll().collectList().block().size();
        plante.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(plante))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Plante in the database
        List<Plante> planteList = planteRepository.findAll().collectList().block();
        assertThat(planteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePlanteWithPatch() throws Exception {
        // Initialize the database
        planteRepository.save(plante).block();

        int databaseSizeBeforeUpdate = planteRepository.findAll().collectList().block().size();

        // Update the plante using partial update
        Plante partialUpdatedPlante = new Plante();
        partialUpdatedPlante.setId(plante.getId());

        partialUpdatedPlante.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPlante.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPlante))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Plante in the database
        List<Plante> planteList = planteRepository.findAll().collectList().block();
        assertThat(planteList).hasSize(databaseSizeBeforeUpdate);
        Plante testPlante = planteList.get(planteList.size() - 1);
        assertThat(testPlante.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testPlante.getRacine()).isEqualTo(DEFAULT_RACINE);
        assertThat(testPlante.getImage()).isEqualTo(DEFAULT_IMAGE);
    }

    @Test
    void fullUpdatePlanteWithPatch() throws Exception {
        // Initialize the database
        planteRepository.save(plante).block();

        int databaseSizeBeforeUpdate = planteRepository.findAll().collectList().block().size();

        // Update the plante using partial update
        Plante partialUpdatedPlante = new Plante();
        partialUpdatedPlante.setId(plante.getId());

        partialUpdatedPlante.libelle(UPDATED_LIBELLE).racine(UPDATED_RACINE).image(UPDATED_IMAGE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPlante.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPlante))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Plante in the database
        List<Plante> planteList = planteRepository.findAll().collectList().block();
        assertThat(planteList).hasSize(databaseSizeBeforeUpdate);
        Plante testPlante = planteList.get(planteList.size() - 1);
        assertThat(testPlante.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testPlante.getRacine()).isEqualTo(UPDATED_RACINE);
        assertThat(testPlante.getImage()).isEqualTo(UPDATED_IMAGE);
    }

    @Test
    void patchNonExistingPlante() throws Exception {
        int databaseSizeBeforeUpdate = planteRepository.findAll().collectList().block().size();
        plante.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, plante.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(plante))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Plante in the database
        List<Plante> planteList = planteRepository.findAll().collectList().block();
        assertThat(planteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPlante() throws Exception {
        int databaseSizeBeforeUpdate = planteRepository.findAll().collectList().block().size();
        plante.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(plante))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Plante in the database
        List<Plante> planteList = planteRepository.findAll().collectList().block();
        assertThat(planteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPlante() throws Exception {
        int databaseSizeBeforeUpdate = planteRepository.findAll().collectList().block().size();
        plante.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(plante))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Plante in the database
        List<Plante> planteList = planteRepository.findAll().collectList().block();
        assertThat(planteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePlante() {
        // Initialize the database
        planteRepository.save(plante).block();

        int databaseSizeBeforeDelete = planteRepository.findAll().collectList().block().size();

        // Delete the plante
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, plante.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Plante> planteList = planteRepository.findAll().collectList().block();
        assertThat(planteList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
