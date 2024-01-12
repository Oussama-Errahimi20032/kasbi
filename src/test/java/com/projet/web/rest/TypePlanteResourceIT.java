package com.projet.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.projet.IntegrationTest;
import com.projet.domain.TypePlante;
import com.projet.repository.EntityManager;
import com.projet.repository.TypePlanteRepository;
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
 * Integration tests for the {@link TypePlanteResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TypePlanteResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_HUMIDITE_MAX = 1;
    private static final Integer UPDATED_HUMIDITE_MAX = 2;

    private static final Integer DEFAULT_HUMIDITE_MIN = 1;
    private static final Integer UPDATED_HUMIDITE_MIN = 2;

    private static final Integer DEFAULT_TEMPERATURE = 1;
    private static final Integer UPDATED_TEMPERATURE = 2;

    private static final String ENTITY_API_URL = "/api/type-plantes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TypePlanteRepository typePlanteRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TypePlante typePlante;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TypePlante createEntity(EntityManager em) {
        TypePlante typePlante = new TypePlante()
            .name(DEFAULT_NAME)
            .humiditeMax(DEFAULT_HUMIDITE_MAX)
            .humiditeMin(DEFAULT_HUMIDITE_MIN)
            .temperature(DEFAULT_TEMPERATURE);
        return typePlante;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TypePlante createUpdatedEntity(EntityManager em) {
        TypePlante typePlante = new TypePlante()
            .name(UPDATED_NAME)
            .humiditeMax(UPDATED_HUMIDITE_MAX)
            .humiditeMin(UPDATED_HUMIDITE_MIN)
            .temperature(UPDATED_TEMPERATURE);
        return typePlante;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TypePlante.class).block();
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
        typePlante = createEntity(em);
    }

    @Test
    void createTypePlante() throws Exception {
        int databaseSizeBeforeCreate = typePlanteRepository.findAll().collectList().block().size();
        // Create the TypePlante
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(typePlante))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the TypePlante in the database
        List<TypePlante> typePlanteList = typePlanteRepository.findAll().collectList().block();
        assertThat(typePlanteList).hasSize(databaseSizeBeforeCreate + 1);
        TypePlante testTypePlante = typePlanteList.get(typePlanteList.size() - 1);
        assertThat(testTypePlante.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTypePlante.getHumiditeMax()).isEqualTo(DEFAULT_HUMIDITE_MAX);
        assertThat(testTypePlante.getHumiditeMin()).isEqualTo(DEFAULT_HUMIDITE_MIN);
        assertThat(testTypePlante.getTemperature()).isEqualTo(DEFAULT_TEMPERATURE);
    }

    @Test
    void createTypePlanteWithExistingId() throws Exception {
        // Create the TypePlante with an existing ID
        typePlante.setId(1L);

        int databaseSizeBeforeCreate = typePlanteRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(typePlante))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TypePlante in the database
        List<TypePlante> typePlanteList = typePlanteRepository.findAll().collectList().block();
        assertThat(typePlanteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllTypePlantesAsStream() {
        // Initialize the database
        typePlanteRepository.save(typePlante).block();

        List<TypePlante> typePlanteList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(TypePlante.class)
            .getResponseBody()
            .filter(typePlante::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(typePlanteList).isNotNull();
        assertThat(typePlanteList).hasSize(1);
        TypePlante testTypePlante = typePlanteList.get(0);
        assertThat(testTypePlante.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTypePlante.getHumiditeMax()).isEqualTo(DEFAULT_HUMIDITE_MAX);
        assertThat(testTypePlante.getHumiditeMin()).isEqualTo(DEFAULT_HUMIDITE_MIN);
        assertThat(testTypePlante.getTemperature()).isEqualTo(DEFAULT_TEMPERATURE);
    }

    @Test
    void getAllTypePlantes() {
        // Initialize the database
        typePlanteRepository.save(typePlante).block();

        // Get all the typePlanteList
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
            .value(hasItem(typePlante.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].humiditeMax")
            .value(hasItem(DEFAULT_HUMIDITE_MAX))
            .jsonPath("$.[*].humiditeMin")
            .value(hasItem(DEFAULT_HUMIDITE_MIN))
            .jsonPath("$.[*].temperature")
            .value(hasItem(DEFAULT_TEMPERATURE));
    }

    @Test
    void getTypePlante() {
        // Initialize the database
        typePlanteRepository.save(typePlante).block();

        // Get the typePlante
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, typePlante.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(typePlante.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.humiditeMax")
            .value(is(DEFAULT_HUMIDITE_MAX))
            .jsonPath("$.humiditeMin")
            .value(is(DEFAULT_HUMIDITE_MIN))
            .jsonPath("$.temperature")
            .value(is(DEFAULT_TEMPERATURE));
    }

    @Test
    void getNonExistingTypePlante() {
        // Get the typePlante
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTypePlante() throws Exception {
        // Initialize the database
        typePlanteRepository.save(typePlante).block();

        int databaseSizeBeforeUpdate = typePlanteRepository.findAll().collectList().block().size();

        // Update the typePlante
        TypePlante updatedTypePlante = typePlanteRepository.findById(typePlante.getId()).block();
        updatedTypePlante
            .name(UPDATED_NAME)
            .humiditeMax(UPDATED_HUMIDITE_MAX)
            .humiditeMin(UPDATED_HUMIDITE_MIN)
            .temperature(UPDATED_TEMPERATURE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedTypePlante.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedTypePlante))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TypePlante in the database
        List<TypePlante> typePlanteList = typePlanteRepository.findAll().collectList().block();
        assertThat(typePlanteList).hasSize(databaseSizeBeforeUpdate);
        TypePlante testTypePlante = typePlanteList.get(typePlanteList.size() - 1);
        assertThat(testTypePlante.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTypePlante.getHumiditeMax()).isEqualTo(UPDATED_HUMIDITE_MAX);
        assertThat(testTypePlante.getHumiditeMin()).isEqualTo(UPDATED_HUMIDITE_MIN);
        assertThat(testTypePlante.getTemperature()).isEqualTo(UPDATED_TEMPERATURE);
    }

    @Test
    void putNonExistingTypePlante() throws Exception {
        int databaseSizeBeforeUpdate = typePlanteRepository.findAll().collectList().block().size();
        typePlante.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, typePlante.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(typePlante))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TypePlante in the database
        List<TypePlante> typePlanteList = typePlanteRepository.findAll().collectList().block();
        assertThat(typePlanteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTypePlante() throws Exception {
        int databaseSizeBeforeUpdate = typePlanteRepository.findAll().collectList().block().size();
        typePlante.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(typePlante))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TypePlante in the database
        List<TypePlante> typePlanteList = typePlanteRepository.findAll().collectList().block();
        assertThat(typePlanteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTypePlante() throws Exception {
        int databaseSizeBeforeUpdate = typePlanteRepository.findAll().collectList().block().size();
        typePlante.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(typePlante))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TypePlante in the database
        List<TypePlante> typePlanteList = typePlanteRepository.findAll().collectList().block();
        assertThat(typePlanteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTypePlanteWithPatch() throws Exception {
        // Initialize the database
        typePlanteRepository.save(typePlante).block();

        int databaseSizeBeforeUpdate = typePlanteRepository.findAll().collectList().block().size();

        // Update the typePlante using partial update
        TypePlante partialUpdatedTypePlante = new TypePlante();
        partialUpdatedTypePlante.setId(typePlante.getId());

        partialUpdatedTypePlante.humiditeMax(UPDATED_HUMIDITE_MAX);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTypePlante.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTypePlante))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TypePlante in the database
        List<TypePlante> typePlanteList = typePlanteRepository.findAll().collectList().block();
        assertThat(typePlanteList).hasSize(databaseSizeBeforeUpdate);
        TypePlante testTypePlante = typePlanteList.get(typePlanteList.size() - 1);
        assertThat(testTypePlante.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTypePlante.getHumiditeMax()).isEqualTo(UPDATED_HUMIDITE_MAX);
        assertThat(testTypePlante.getHumiditeMin()).isEqualTo(DEFAULT_HUMIDITE_MIN);
        assertThat(testTypePlante.getTemperature()).isEqualTo(DEFAULT_TEMPERATURE);
    }

    @Test
    void fullUpdateTypePlanteWithPatch() throws Exception {
        // Initialize the database
        typePlanteRepository.save(typePlante).block();

        int databaseSizeBeforeUpdate = typePlanteRepository.findAll().collectList().block().size();

        // Update the typePlante using partial update
        TypePlante partialUpdatedTypePlante = new TypePlante();
        partialUpdatedTypePlante.setId(typePlante.getId());

        partialUpdatedTypePlante
            .name(UPDATED_NAME)
            .humiditeMax(UPDATED_HUMIDITE_MAX)
            .humiditeMin(UPDATED_HUMIDITE_MIN)
            .temperature(UPDATED_TEMPERATURE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTypePlante.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTypePlante))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TypePlante in the database
        List<TypePlante> typePlanteList = typePlanteRepository.findAll().collectList().block();
        assertThat(typePlanteList).hasSize(databaseSizeBeforeUpdate);
        TypePlante testTypePlante = typePlanteList.get(typePlanteList.size() - 1);
        assertThat(testTypePlante.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTypePlante.getHumiditeMax()).isEqualTo(UPDATED_HUMIDITE_MAX);
        assertThat(testTypePlante.getHumiditeMin()).isEqualTo(UPDATED_HUMIDITE_MIN);
        assertThat(testTypePlante.getTemperature()).isEqualTo(UPDATED_TEMPERATURE);
    }

    @Test
    void patchNonExistingTypePlante() throws Exception {
        int databaseSizeBeforeUpdate = typePlanteRepository.findAll().collectList().block().size();
        typePlante.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, typePlante.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(typePlante))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TypePlante in the database
        List<TypePlante> typePlanteList = typePlanteRepository.findAll().collectList().block();
        assertThat(typePlanteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTypePlante() throws Exception {
        int databaseSizeBeforeUpdate = typePlanteRepository.findAll().collectList().block().size();
        typePlante.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(typePlante))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TypePlante in the database
        List<TypePlante> typePlanteList = typePlanteRepository.findAll().collectList().block();
        assertThat(typePlanteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTypePlante() throws Exception {
        int databaseSizeBeforeUpdate = typePlanteRepository.findAll().collectList().block().size();
        typePlante.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(typePlante))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TypePlante in the database
        List<TypePlante> typePlanteList = typePlanteRepository.findAll().collectList().block();
        assertThat(typePlanteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTypePlante() {
        // Initialize the database
        typePlanteRepository.save(typePlante).block();

        int databaseSizeBeforeDelete = typePlanteRepository.findAll().collectList().block().size();

        // Delete the typePlante
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, typePlante.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<TypePlante> typePlanteList = typePlanteRepository.findAll().collectList().block();
        assertThat(typePlanteList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
