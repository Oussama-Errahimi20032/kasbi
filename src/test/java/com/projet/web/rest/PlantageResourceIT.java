package com.projet.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.projet.IntegrationTest;
import com.projet.domain.Plantage;
import com.projet.repository.EntityManager;
import com.projet.repository.PlantageRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link PlantageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PlantageResourceIT {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Integer DEFAULT_NOMBRE = 1;
    private static final Integer UPDATED_NOMBRE = 2;

    private static final String ENTITY_API_URL = "/api/plantages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PlantageRepository plantageRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Plantage plantage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Plantage createEntity(EntityManager em) {
        Plantage plantage = new Plantage().date(DEFAULT_DATE).nombre(DEFAULT_NOMBRE);
        return plantage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Plantage createUpdatedEntity(EntityManager em) {
        Plantage plantage = new Plantage().date(UPDATED_DATE).nombre(UPDATED_NOMBRE);
        return plantage;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Plantage.class).block();
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
        plantage = createEntity(em);
    }

    @Test
    void createPlantage() throws Exception {
        int databaseSizeBeforeCreate = plantageRepository.findAll().collectList().block().size();
        // Create the Plantage
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(plantage))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Plantage in the database
        List<Plantage> plantageList = plantageRepository.findAll().collectList().block();
        assertThat(plantageList).hasSize(databaseSizeBeforeCreate + 1);
        Plantage testPlantage = plantageList.get(plantageList.size() - 1);
        assertThat(testPlantage.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testPlantage.getNombre()).isEqualTo(DEFAULT_NOMBRE);
    }

    @Test
    void createPlantageWithExistingId() throws Exception {
        // Create the Plantage with an existing ID
        plantage.setId(1L);

        int databaseSizeBeforeCreate = plantageRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(plantage))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Plantage in the database
        List<Plantage> plantageList = plantageRepository.findAll().collectList().block();
        assertThat(plantageList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPlantagesAsStream() {
        // Initialize the database
        plantageRepository.save(plantage).block();

        List<Plantage> plantageList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Plantage.class)
            .getResponseBody()
            .filter(plantage::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(plantageList).isNotNull();
        assertThat(plantageList).hasSize(1);
        Plantage testPlantage = plantageList.get(0);
        assertThat(testPlantage.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testPlantage.getNombre()).isEqualTo(DEFAULT_NOMBRE);
    }

    @Test
    void getAllPlantages() {
        // Initialize the database
        plantageRepository.save(plantage).block();

        // Get all the plantageList
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
            .value(hasItem(plantage.getId().intValue()))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE.toString()))
            .jsonPath("$.[*].nombre")
            .value(hasItem(DEFAULT_NOMBRE));
    }

    @Test
    void getPlantage() {
        // Initialize the database
        plantageRepository.save(plantage).block();

        // Get the plantage
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, plantage.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(plantage.getId().intValue()))
            .jsonPath("$.date")
            .value(is(DEFAULT_DATE.toString()))
            .jsonPath("$.nombre")
            .value(is(DEFAULT_NOMBRE));
    }

    @Test
    void getNonExistingPlantage() {
        // Get the plantage
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPlantage() throws Exception {
        // Initialize the database
        plantageRepository.save(plantage).block();

        int databaseSizeBeforeUpdate = plantageRepository.findAll().collectList().block().size();

        // Update the plantage
        Plantage updatedPlantage = plantageRepository.findById(plantage.getId()).block();
        updatedPlantage.date(UPDATED_DATE).nombre(UPDATED_NOMBRE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPlantage.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPlantage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Plantage in the database
        List<Plantage> plantageList = plantageRepository.findAll().collectList().block();
        assertThat(plantageList).hasSize(databaseSizeBeforeUpdate);
        Plantage testPlantage = plantageList.get(plantageList.size() - 1);
        assertThat(testPlantage.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testPlantage.getNombre()).isEqualTo(UPDATED_NOMBRE);
    }

    @Test
    void putNonExistingPlantage() throws Exception {
        int databaseSizeBeforeUpdate = plantageRepository.findAll().collectList().block().size();
        plantage.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, plantage.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(plantage))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Plantage in the database
        List<Plantage> plantageList = plantageRepository.findAll().collectList().block();
        assertThat(plantageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPlantage() throws Exception {
        int databaseSizeBeforeUpdate = plantageRepository.findAll().collectList().block().size();
        plantage.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(plantage))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Plantage in the database
        List<Plantage> plantageList = plantageRepository.findAll().collectList().block();
        assertThat(plantageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPlantage() throws Exception {
        int databaseSizeBeforeUpdate = plantageRepository.findAll().collectList().block().size();
        plantage.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(plantage))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Plantage in the database
        List<Plantage> plantageList = plantageRepository.findAll().collectList().block();
        assertThat(plantageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePlantageWithPatch() throws Exception {
        // Initialize the database
        plantageRepository.save(plantage).block();

        int databaseSizeBeforeUpdate = plantageRepository.findAll().collectList().block().size();

        // Update the plantage using partial update
        Plantage partialUpdatedPlantage = new Plantage();
        partialUpdatedPlantage.setId(plantage.getId());

        partialUpdatedPlantage.date(UPDATED_DATE).nombre(UPDATED_NOMBRE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPlantage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPlantage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Plantage in the database
        List<Plantage> plantageList = plantageRepository.findAll().collectList().block();
        assertThat(plantageList).hasSize(databaseSizeBeforeUpdate);
        Plantage testPlantage = plantageList.get(plantageList.size() - 1);
        assertThat(testPlantage.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testPlantage.getNombre()).isEqualTo(UPDATED_NOMBRE);
    }

    @Test
    void fullUpdatePlantageWithPatch() throws Exception {
        // Initialize the database
        plantageRepository.save(plantage).block();

        int databaseSizeBeforeUpdate = plantageRepository.findAll().collectList().block().size();

        // Update the plantage using partial update
        Plantage partialUpdatedPlantage = new Plantage();
        partialUpdatedPlantage.setId(plantage.getId());

        partialUpdatedPlantage.date(UPDATED_DATE).nombre(UPDATED_NOMBRE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPlantage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPlantage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Plantage in the database
        List<Plantage> plantageList = plantageRepository.findAll().collectList().block();
        assertThat(plantageList).hasSize(databaseSizeBeforeUpdate);
        Plantage testPlantage = plantageList.get(plantageList.size() - 1);
        assertThat(testPlantage.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testPlantage.getNombre()).isEqualTo(UPDATED_NOMBRE);
    }

    @Test
    void patchNonExistingPlantage() throws Exception {
        int databaseSizeBeforeUpdate = plantageRepository.findAll().collectList().block().size();
        plantage.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, plantage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(plantage))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Plantage in the database
        List<Plantage> plantageList = plantageRepository.findAll().collectList().block();
        assertThat(plantageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPlantage() throws Exception {
        int databaseSizeBeforeUpdate = plantageRepository.findAll().collectList().block().size();
        plantage.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(plantage))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Plantage in the database
        List<Plantage> plantageList = plantageRepository.findAll().collectList().block();
        assertThat(plantageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPlantage() throws Exception {
        int databaseSizeBeforeUpdate = plantageRepository.findAll().collectList().block().size();
        plantage.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(plantage))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Plantage in the database
        List<Plantage> plantageList = plantageRepository.findAll().collectList().block();
        assertThat(plantageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePlantage() {
        // Initialize the database
        plantageRepository.save(plantage).block();

        int databaseSizeBeforeDelete = plantageRepository.findAll().collectList().block().size();

        // Delete the plantage
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, plantage.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Plantage> plantageList = plantageRepository.findAll().collectList().block();
        assertThat(plantageList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
