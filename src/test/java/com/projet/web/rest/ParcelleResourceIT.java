package com.projet.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.projet.IntegrationTest;
import com.projet.domain.Parcelle;
import com.projet.repository.EntityManager;
import com.projet.repository.ParcelleRepository;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

/**
 * Integration tests for the {@link ParcelleResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ParcelleResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String DEFAULT_PHOTO = "AAAAAAAAAA";
    private static final String UPDATED_PHOTO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/parcelles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ParcelleRepository parcelleRepository;

    @Mock
    private ParcelleRepository parcelleRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Parcelle parcelle;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Parcelle createEntity(EntityManager em) {
        Parcelle parcelle = new Parcelle().libelle(DEFAULT_LIBELLE).photo(DEFAULT_PHOTO);
        return parcelle;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Parcelle createUpdatedEntity(EntityManager em) {
        Parcelle parcelle = new Parcelle().libelle(UPDATED_LIBELLE).photo(UPDATED_PHOTO);
        return parcelle;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_parcelle__plantes").block();
            em.deleteAll(Parcelle.class).block();
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
        parcelle = createEntity(em);
    }

    @Test
    void createParcelle() throws Exception {
        int databaseSizeBeforeCreate = parcelleRepository.findAll().collectList().block().size();
        // Create the Parcelle
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parcelle))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll().collectList().block();
        assertThat(parcelleList).hasSize(databaseSizeBeforeCreate + 1);
        Parcelle testParcelle = parcelleList.get(parcelleList.size() - 1);
        assertThat(testParcelle.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
        assertThat(testParcelle.getPhoto()).isEqualTo(DEFAULT_PHOTO);
    }

    @Test
    void createParcelleWithExistingId() throws Exception {
        // Create the Parcelle with an existing ID
        parcelle.setId(1L);

        int databaseSizeBeforeCreate = parcelleRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parcelle))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll().collectList().block();
        assertThat(parcelleList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllParcellesAsStream() {
        // Initialize the database
        parcelleRepository.save(parcelle).block();

        List<Parcelle> parcelleList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Parcelle.class)
            .getResponseBody()
            .filter(parcelle::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(parcelleList).isNotNull();
        assertThat(parcelleList).hasSize(1);
        Parcelle testParcelle = parcelleList.get(0);
        assertThat(testParcelle.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
        assertThat(testParcelle.getPhoto()).isEqualTo(DEFAULT_PHOTO);
    }

    @Test
    void getAllParcelles() {
        // Initialize the database
        parcelleRepository.save(parcelle).block();

        // Get all the parcelleList
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
            .value(hasItem(parcelle.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE))
            .jsonPath("$.[*].photo")
            .value(hasItem(DEFAULT_PHOTO));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllParcellesWithEagerRelationshipsIsEnabled() {
        when(parcelleRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(parcelleRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllParcellesWithEagerRelationshipsIsNotEnabled() {
        when(parcelleRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(parcelleRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getParcelle() {
        // Initialize the database
        parcelleRepository.save(parcelle).block();

        // Get the parcelle
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, parcelle.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(parcelle.getId().intValue()))
            .jsonPath("$.libelle")
            .value(is(DEFAULT_LIBELLE))
            .jsonPath("$.photo")
            .value(is(DEFAULT_PHOTO));
    }

    @Test
    void getNonExistingParcelle() {
        // Get the parcelle
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingParcelle() throws Exception {
        // Initialize the database
        parcelleRepository.save(parcelle).block();

        int databaseSizeBeforeUpdate = parcelleRepository.findAll().collectList().block().size();

        // Update the parcelle
        Parcelle updatedParcelle = parcelleRepository.findById(parcelle.getId()).block();
        updatedParcelle.libelle(UPDATED_LIBELLE).photo(UPDATED_PHOTO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedParcelle.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedParcelle))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll().collectList().block();
        assertThat(parcelleList).hasSize(databaseSizeBeforeUpdate);
        Parcelle testParcelle = parcelleList.get(parcelleList.size() - 1);
        assertThat(testParcelle.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testParcelle.getPhoto()).isEqualTo(UPDATED_PHOTO);
    }

    @Test
    void putNonExistingParcelle() throws Exception {
        int databaseSizeBeforeUpdate = parcelleRepository.findAll().collectList().block().size();
        parcelle.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, parcelle.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parcelle))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll().collectList().block();
        assertThat(parcelleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchParcelle() throws Exception {
        int databaseSizeBeforeUpdate = parcelleRepository.findAll().collectList().block().size();
        parcelle.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parcelle))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll().collectList().block();
        assertThat(parcelleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamParcelle() throws Exception {
        int databaseSizeBeforeUpdate = parcelleRepository.findAll().collectList().block().size();
        parcelle.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parcelle))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll().collectList().block();
        assertThat(parcelleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateParcelleWithPatch() throws Exception {
        // Initialize the database
        parcelleRepository.save(parcelle).block();

        int databaseSizeBeforeUpdate = parcelleRepository.findAll().collectList().block().size();

        // Update the parcelle using partial update
        Parcelle partialUpdatedParcelle = new Parcelle();
        partialUpdatedParcelle.setId(parcelle.getId());

        partialUpdatedParcelle.photo(UPDATED_PHOTO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedParcelle.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedParcelle))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll().collectList().block();
        assertThat(parcelleList).hasSize(databaseSizeBeforeUpdate);
        Parcelle testParcelle = parcelleList.get(parcelleList.size() - 1);
        assertThat(testParcelle.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
        assertThat(testParcelle.getPhoto()).isEqualTo(UPDATED_PHOTO);
    }

    @Test
    void fullUpdateParcelleWithPatch() throws Exception {
        // Initialize the database
        parcelleRepository.save(parcelle).block();

        int databaseSizeBeforeUpdate = parcelleRepository.findAll().collectList().block().size();

        // Update the parcelle using partial update
        Parcelle partialUpdatedParcelle = new Parcelle();
        partialUpdatedParcelle.setId(parcelle.getId());

        partialUpdatedParcelle.libelle(UPDATED_LIBELLE).photo(UPDATED_PHOTO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedParcelle.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedParcelle))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll().collectList().block();
        assertThat(parcelleList).hasSize(databaseSizeBeforeUpdate);
        Parcelle testParcelle = parcelleList.get(parcelleList.size() - 1);
        assertThat(testParcelle.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testParcelle.getPhoto()).isEqualTo(UPDATED_PHOTO);
    }

    @Test
    void patchNonExistingParcelle() throws Exception {
        int databaseSizeBeforeUpdate = parcelleRepository.findAll().collectList().block().size();
        parcelle.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, parcelle.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(parcelle))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll().collectList().block();
        assertThat(parcelleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchParcelle() throws Exception {
        int databaseSizeBeforeUpdate = parcelleRepository.findAll().collectList().block().size();
        parcelle.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(parcelle))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll().collectList().block();
        assertThat(parcelleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamParcelle() throws Exception {
        int databaseSizeBeforeUpdate = parcelleRepository.findAll().collectList().block().size();
        parcelle.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(parcelle))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll().collectList().block();
        assertThat(parcelleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteParcelle() {
        // Initialize the database
        parcelleRepository.save(parcelle).block();

        int databaseSizeBeforeDelete = parcelleRepository.findAll().collectList().block().size();

        // Delete the parcelle
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, parcelle.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Parcelle> parcelleList = parcelleRepository.findAll().collectList().block();
        assertThat(parcelleList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
