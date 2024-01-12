package com.projet.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.projet.IntegrationTest;
import com.projet.domain.Ferme;
import com.projet.repository.EntityManager;
import com.projet.repository.FermeRepository;
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
 * Integration tests for the {@link FermeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class FermeResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String DEFAULT_PHOTO = "AAAAAAAAAA";
    private static final String UPDATED_PHOTO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/fermes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FermeRepository fermeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Ferme ferme;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ferme createEntity(EntityManager em) {
        Ferme ferme = new Ferme().libelle(DEFAULT_LIBELLE).photo(DEFAULT_PHOTO);
        return ferme;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ferme createUpdatedEntity(EntityManager em) {
        Ferme ferme = new Ferme().libelle(UPDATED_LIBELLE).photo(UPDATED_PHOTO);
        return ferme;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Ferme.class).block();
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
        ferme = createEntity(em);
    }

    @Test
    void createFerme() throws Exception {
        int databaseSizeBeforeCreate = fermeRepository.findAll().collectList().block().size();
        // Create the Ferme
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ferme))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Ferme in the database
        List<Ferme> fermeList = fermeRepository.findAll().collectList().block();
        assertThat(fermeList).hasSize(databaseSizeBeforeCreate + 1);
        Ferme testFerme = fermeList.get(fermeList.size() - 1);
        assertThat(testFerme.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
        assertThat(testFerme.getPhoto()).isEqualTo(DEFAULT_PHOTO);
    }

    @Test
    void createFermeWithExistingId() throws Exception {
        // Create the Ferme with an existing ID
        ferme.setId(1L);

        int databaseSizeBeforeCreate = fermeRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ferme))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ferme in the database
        List<Ferme> fermeList = fermeRepository.findAll().collectList().block();
        assertThat(fermeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllFermesAsStream() {
        // Initialize the database
        fermeRepository.save(ferme).block();

        List<Ferme> fermeList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Ferme.class)
            .getResponseBody()
            .filter(ferme::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(fermeList).isNotNull();
        assertThat(fermeList).hasSize(1);
        Ferme testFerme = fermeList.get(0);
        assertThat(testFerme.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
        assertThat(testFerme.getPhoto()).isEqualTo(DEFAULT_PHOTO);
    }

    @Test
    void getAllFermes() {
        // Initialize the database
        fermeRepository.save(ferme).block();

        // Get all the fermeList
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
            .value(hasItem(ferme.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE))
            .jsonPath("$.[*].photo")
            .value(hasItem(DEFAULT_PHOTO));
    }

    @Test
    void getFerme() {
        // Initialize the database
        fermeRepository.save(ferme).block();

        // Get the ferme
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, ferme.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(ferme.getId().intValue()))
            .jsonPath("$.libelle")
            .value(is(DEFAULT_LIBELLE))
            .jsonPath("$.photo")
            .value(is(DEFAULT_PHOTO));
    }

    @Test
    void getNonExistingFerme() {
        // Get the ferme
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingFerme() throws Exception {
        // Initialize the database
        fermeRepository.save(ferme).block();

        int databaseSizeBeforeUpdate = fermeRepository.findAll().collectList().block().size();

        // Update the ferme
        Ferme updatedFerme = fermeRepository.findById(ferme.getId()).block();
        updatedFerme.libelle(UPDATED_LIBELLE).photo(UPDATED_PHOTO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedFerme.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedFerme))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Ferme in the database
        List<Ferme> fermeList = fermeRepository.findAll().collectList().block();
        assertThat(fermeList).hasSize(databaseSizeBeforeUpdate);
        Ferme testFerme = fermeList.get(fermeList.size() - 1);
        assertThat(testFerme.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testFerme.getPhoto()).isEqualTo(UPDATED_PHOTO);
    }

    @Test
    void putNonExistingFerme() throws Exception {
        int databaseSizeBeforeUpdate = fermeRepository.findAll().collectList().block().size();
        ferme.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, ferme.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ferme))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ferme in the database
        List<Ferme> fermeList = fermeRepository.findAll().collectList().block();
        assertThat(fermeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchFerme() throws Exception {
        int databaseSizeBeforeUpdate = fermeRepository.findAll().collectList().block().size();
        ferme.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ferme))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ferme in the database
        List<Ferme> fermeList = fermeRepository.findAll().collectList().block();
        assertThat(fermeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamFerme() throws Exception {
        int databaseSizeBeforeUpdate = fermeRepository.findAll().collectList().block().size();
        ferme.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ferme))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Ferme in the database
        List<Ferme> fermeList = fermeRepository.findAll().collectList().block();
        assertThat(fermeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateFermeWithPatch() throws Exception {
        // Initialize the database
        fermeRepository.save(ferme).block();

        int databaseSizeBeforeUpdate = fermeRepository.findAll().collectList().block().size();

        // Update the ferme using partial update
        Ferme partialUpdatedFerme = new Ferme();
        partialUpdatedFerme.setId(ferme.getId());

        partialUpdatedFerme.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFerme.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFerme))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Ferme in the database
        List<Ferme> fermeList = fermeRepository.findAll().collectList().block();
        assertThat(fermeList).hasSize(databaseSizeBeforeUpdate);
        Ferme testFerme = fermeList.get(fermeList.size() - 1);
        assertThat(testFerme.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testFerme.getPhoto()).isEqualTo(DEFAULT_PHOTO);
    }

    @Test
    void fullUpdateFermeWithPatch() throws Exception {
        // Initialize the database
        fermeRepository.save(ferme).block();

        int databaseSizeBeforeUpdate = fermeRepository.findAll().collectList().block().size();

        // Update the ferme using partial update
        Ferme partialUpdatedFerme = new Ferme();
        partialUpdatedFerme.setId(ferme.getId());

        partialUpdatedFerme.libelle(UPDATED_LIBELLE).photo(UPDATED_PHOTO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFerme.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFerme))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Ferme in the database
        List<Ferme> fermeList = fermeRepository.findAll().collectList().block();
        assertThat(fermeList).hasSize(databaseSizeBeforeUpdate);
        Ferme testFerme = fermeList.get(fermeList.size() - 1);
        assertThat(testFerme.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testFerme.getPhoto()).isEqualTo(UPDATED_PHOTO);
    }

    @Test
    void patchNonExistingFerme() throws Exception {
        int databaseSizeBeforeUpdate = fermeRepository.findAll().collectList().block().size();
        ferme.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, ferme.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(ferme))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ferme in the database
        List<Ferme> fermeList = fermeRepository.findAll().collectList().block();
        assertThat(fermeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchFerme() throws Exception {
        int databaseSizeBeforeUpdate = fermeRepository.findAll().collectList().block().size();
        ferme.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(ferme))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ferme in the database
        List<Ferme> fermeList = fermeRepository.findAll().collectList().block();
        assertThat(fermeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamFerme() throws Exception {
        int databaseSizeBeforeUpdate = fermeRepository.findAll().collectList().block().size();
        ferme.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(ferme))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Ferme in the database
        List<Ferme> fermeList = fermeRepository.findAll().collectList().block();
        assertThat(fermeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteFerme() {
        // Initialize the database
        fermeRepository.save(ferme).block();

        int databaseSizeBeforeDelete = fermeRepository.findAll().collectList().block().size();

        // Delete the ferme
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, ferme.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Ferme> fermeList = fermeRepository.findAll().collectList().block();
        assertThat(fermeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
