package com.projet.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.projet.IntegrationTest;
import com.projet.domain.Admin;
import com.projet.repository.AdminRepository;
import com.projet.repository.EntityManager;
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
 * Integration tests for the {@link AdminResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AdminResourceIT {

    private static final String ENTITY_API_URL = "/api/admins";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Admin admin;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Admin createEntity(EntityManager em) {
        Admin admin = new Admin();
        return admin;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Admin createUpdatedEntity(EntityManager em) {
        Admin admin = new Admin();
        return admin;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Admin.class).block();
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
        admin = createEntity(em);
    }

    @Test
    void createAdmin() throws Exception {
        int databaseSizeBeforeCreate = adminRepository.findAll().collectList().block().size();
        // Create the Admin
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(admin))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Admin in the database
        List<Admin> adminList = adminRepository.findAll().collectList().block();
        assertThat(adminList).hasSize(databaseSizeBeforeCreate + 1);
        Admin testAdmin = adminList.get(adminList.size() - 1);
    }

    @Test
    void createAdminWithExistingId() throws Exception {
        // Create the Admin with an existing ID
        admin.setId(1L);

        int databaseSizeBeforeCreate = adminRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(admin))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Admin in the database
        List<Admin> adminList = adminRepository.findAll().collectList().block();
        assertThat(adminList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllAdminsAsStream() {
        // Initialize the database
        adminRepository.save(admin).block();

        List<Admin> adminList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Admin.class)
            .getResponseBody()
            .filter(admin::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(adminList).isNotNull();
        assertThat(adminList).hasSize(1);
        Admin testAdmin = adminList.get(0);
    }

    @Test
    void getAllAdmins() {
        // Initialize the database
        adminRepository.save(admin).block();

        // Get all the adminList
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
            .value(hasItem(admin.getId().intValue()));
    }

    @Test
    void getAdmin() {
        // Initialize the database
        adminRepository.save(admin).block();

        // Get the admin
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, admin.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(admin.getId().intValue()));
    }

    @Test
    void getNonExistingAdmin() {
        // Get the admin
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAdmin() throws Exception {
        // Initialize the database
        adminRepository.save(admin).block();

        int databaseSizeBeforeUpdate = adminRepository.findAll().collectList().block().size();

        // Update the admin
        Admin updatedAdmin = adminRepository.findById(admin.getId()).block();

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedAdmin.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedAdmin))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Admin in the database
        List<Admin> adminList = adminRepository.findAll().collectList().block();
        assertThat(adminList).hasSize(databaseSizeBeforeUpdate);
        Admin testAdmin = adminList.get(adminList.size() - 1);
    }

    @Test
    void putNonExistingAdmin() throws Exception {
        int databaseSizeBeforeUpdate = adminRepository.findAll().collectList().block().size();
        admin.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, admin.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(admin))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Admin in the database
        List<Admin> adminList = adminRepository.findAll().collectList().block();
        assertThat(adminList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAdmin() throws Exception {
        int databaseSizeBeforeUpdate = adminRepository.findAll().collectList().block().size();
        admin.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(admin))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Admin in the database
        List<Admin> adminList = adminRepository.findAll().collectList().block();
        assertThat(adminList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAdmin() throws Exception {
        int databaseSizeBeforeUpdate = adminRepository.findAll().collectList().block().size();
        admin.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(admin))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Admin in the database
        List<Admin> adminList = adminRepository.findAll().collectList().block();
        assertThat(adminList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAdminWithPatch() throws Exception {
        // Initialize the database
        adminRepository.save(admin).block();

        int databaseSizeBeforeUpdate = adminRepository.findAll().collectList().block().size();

        // Update the admin using partial update
        Admin partialUpdatedAdmin = new Admin();
        partialUpdatedAdmin.setId(admin.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAdmin.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAdmin))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Admin in the database
        List<Admin> adminList = adminRepository.findAll().collectList().block();
        assertThat(adminList).hasSize(databaseSizeBeforeUpdate);
        Admin testAdmin = adminList.get(adminList.size() - 1);
    }

    @Test
    void fullUpdateAdminWithPatch() throws Exception {
        // Initialize the database
        adminRepository.save(admin).block();

        int databaseSizeBeforeUpdate = adminRepository.findAll().collectList().block().size();

        // Update the admin using partial update
        Admin partialUpdatedAdmin = new Admin();
        partialUpdatedAdmin.setId(admin.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAdmin.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAdmin))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Admin in the database
        List<Admin> adminList = adminRepository.findAll().collectList().block();
        assertThat(adminList).hasSize(databaseSizeBeforeUpdate);
        Admin testAdmin = adminList.get(adminList.size() - 1);
    }

    @Test
    void patchNonExistingAdmin() throws Exception {
        int databaseSizeBeforeUpdate = adminRepository.findAll().collectList().block().size();
        admin.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, admin.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(admin))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Admin in the database
        List<Admin> adminList = adminRepository.findAll().collectList().block();
        assertThat(adminList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAdmin() throws Exception {
        int databaseSizeBeforeUpdate = adminRepository.findAll().collectList().block().size();
        admin.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(admin))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Admin in the database
        List<Admin> adminList = adminRepository.findAll().collectList().block();
        assertThat(adminList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAdmin() throws Exception {
        int databaseSizeBeforeUpdate = adminRepository.findAll().collectList().block().size();
        admin.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(admin))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Admin in the database
        List<Admin> adminList = adminRepository.findAll().collectList().block();
        assertThat(adminList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAdmin() {
        // Initialize the database
        adminRepository.save(admin).block();

        int databaseSizeBeforeDelete = adminRepository.findAll().collectList().block().size();

        // Delete the admin
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, admin.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Admin> adminList = adminRepository.findAll().collectList().block();
        assertThat(adminList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
