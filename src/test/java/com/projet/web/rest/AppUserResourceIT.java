package com.projet.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.projet.IntegrationTest;
import com.projet.domain.AppUser;
import com.projet.repository.AppUserRepository;
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
 * Integration tests for the {@link AppUserResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AppUserResourceIT {

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_ROLE = "AAAAAAAAAA";
    private static final String UPDATED_ROLE = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/app-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private AppUser appUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppUser createEntity(EntityManager em) {
        AppUser appUser = new AppUser()
            .username(DEFAULT_USERNAME)
            .email(DEFAULT_EMAIL)
            .password(DEFAULT_PASSWORD)
            .address(DEFAULT_ADDRESS)
            .phone(DEFAULT_PHONE)
            .role(DEFAULT_ROLE)
            .image(DEFAULT_IMAGE);
        return appUser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppUser createUpdatedEntity(EntityManager em) {
        AppUser appUser = new AppUser()
            .username(UPDATED_USERNAME)
            .email(UPDATED_EMAIL)
            .password(UPDATED_PASSWORD)
            .address(UPDATED_ADDRESS)
            .phone(UPDATED_PHONE)
            .role(UPDATED_ROLE)
            .image(UPDATED_IMAGE);
        return appUser;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(AppUser.class).block();
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
        appUser = createEntity(em);
    }

    @Test
    void createAppUser() throws Exception {
        int databaseSizeBeforeCreate = appUserRepository.findAll().collectList().block().size();
        // Create the AppUser
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUser))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeCreate + 1);
        AppUser testAppUser = appUserList.get(appUserList.size() - 1);
        assertThat(testAppUser.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testAppUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testAppUser.getPassword()).isEqualTo(DEFAULT_PASSWORD);
        assertThat(testAppUser.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testAppUser.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testAppUser.getRole()).isEqualTo(DEFAULT_ROLE);
        assertThat(testAppUser.getImage()).isEqualTo(DEFAULT_IMAGE);
    }

    @Test
    void createAppUserWithExistingId() throws Exception {
        // Create the AppUser with an existing ID
        appUser.setId(1L);

        int databaseSizeBeforeCreate = appUserRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllAppUsersAsStream() {
        // Initialize the database
        appUserRepository.save(appUser).block();

        List<AppUser> appUserList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(AppUser.class)
            .getResponseBody()
            .filter(appUser::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(appUserList).isNotNull();
        assertThat(appUserList).hasSize(1);
        AppUser testAppUser = appUserList.get(0);
        assertThat(testAppUser.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testAppUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testAppUser.getPassword()).isEqualTo(DEFAULT_PASSWORD);
        assertThat(testAppUser.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testAppUser.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testAppUser.getRole()).isEqualTo(DEFAULT_ROLE);
        assertThat(testAppUser.getImage()).isEqualTo(DEFAULT_IMAGE);
    }

    @Test
    void getAllAppUsers() {
        // Initialize the database
        appUserRepository.save(appUser).block();

        // Get all the appUserList
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
            .value(hasItem(appUser.getId().intValue()))
            .jsonPath("$.[*].username")
            .value(hasItem(DEFAULT_USERNAME))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].password")
            .value(hasItem(DEFAULT_PASSWORD))
            .jsonPath("$.[*].address")
            .value(hasItem(DEFAULT_ADDRESS))
            .jsonPath("$.[*].phone")
            .value(hasItem(DEFAULT_PHONE))
            .jsonPath("$.[*].role")
            .value(hasItem(DEFAULT_ROLE))
            .jsonPath("$.[*].image")
            .value(hasItem(DEFAULT_IMAGE));
    }

    @Test
    void getAppUser() {
        // Initialize the database
        appUserRepository.save(appUser).block();

        // Get the appUser
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, appUser.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(appUser.getId().intValue()))
            .jsonPath("$.username")
            .value(is(DEFAULT_USERNAME))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL))
            .jsonPath("$.password")
            .value(is(DEFAULT_PASSWORD))
            .jsonPath("$.address")
            .value(is(DEFAULT_ADDRESS))
            .jsonPath("$.phone")
            .value(is(DEFAULT_PHONE))
            .jsonPath("$.role")
            .value(is(DEFAULT_ROLE))
            .jsonPath("$.image")
            .value(is(DEFAULT_IMAGE));
    }

    @Test
    void getNonExistingAppUser() {
        // Get the appUser
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAppUser() throws Exception {
        // Initialize the database
        appUserRepository.save(appUser).block();

        int databaseSizeBeforeUpdate = appUserRepository.findAll().collectList().block().size();

        // Update the appUser
        AppUser updatedAppUser = appUserRepository.findById(appUser.getId()).block();
        updatedAppUser
            .username(UPDATED_USERNAME)
            .email(UPDATED_EMAIL)
            .password(UPDATED_PASSWORD)
            .address(UPDATED_ADDRESS)
            .phone(UPDATED_PHONE)
            .role(UPDATED_ROLE)
            .image(UPDATED_IMAGE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedAppUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedAppUser))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
        AppUser testAppUser = appUserList.get(appUserList.size() - 1);
        assertThat(testAppUser.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testAppUser.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testAppUser.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testAppUser.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testAppUser.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testAppUser.getRole()).isEqualTo(UPDATED_ROLE);
        assertThat(testAppUser.getImage()).isEqualTo(UPDATED_IMAGE);
    }

    @Test
    void putNonExistingAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().collectList().block().size();
        appUser.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, appUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().collectList().block().size();
        appUser.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().collectList().block().size();
        appUser.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUser))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAppUserWithPatch() throws Exception {
        // Initialize the database
        appUserRepository.save(appUser).block();

        int databaseSizeBeforeUpdate = appUserRepository.findAll().collectList().block().size();

        // Update the appUser using partial update
        AppUser partialUpdatedAppUser = new AppUser();
        partialUpdatedAppUser.setId(appUser.getId());

        partialUpdatedAppUser.username(UPDATED_USERNAME).email(UPDATED_EMAIL).password(UPDATED_PASSWORD).address(UPDATED_ADDRESS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAppUser.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAppUser))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
        AppUser testAppUser = appUserList.get(appUserList.size() - 1);
        assertThat(testAppUser.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testAppUser.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testAppUser.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testAppUser.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testAppUser.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testAppUser.getRole()).isEqualTo(DEFAULT_ROLE);
        assertThat(testAppUser.getImage()).isEqualTo(DEFAULT_IMAGE);
    }

    @Test
    void fullUpdateAppUserWithPatch() throws Exception {
        // Initialize the database
        appUserRepository.save(appUser).block();

        int databaseSizeBeforeUpdate = appUserRepository.findAll().collectList().block().size();

        // Update the appUser using partial update
        AppUser partialUpdatedAppUser = new AppUser();
        partialUpdatedAppUser.setId(appUser.getId());

        partialUpdatedAppUser
            .username(UPDATED_USERNAME)
            .email(UPDATED_EMAIL)
            .password(UPDATED_PASSWORD)
            .address(UPDATED_ADDRESS)
            .phone(UPDATED_PHONE)
            .role(UPDATED_ROLE)
            .image(UPDATED_IMAGE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAppUser.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAppUser))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
        AppUser testAppUser = appUserList.get(appUserList.size() - 1);
        assertThat(testAppUser.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testAppUser.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testAppUser.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testAppUser.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testAppUser.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testAppUser.getRole()).isEqualTo(UPDATED_ROLE);
        assertThat(testAppUser.getImage()).isEqualTo(UPDATED_IMAGE);
    }

    @Test
    void patchNonExistingAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().collectList().block().size();
        appUser.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, appUser.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().collectList().block().size();
        appUser.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().collectList().block().size();
        appUser.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUser))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAppUser() {
        // Initialize the database
        appUserRepository.save(appUser).block();

        int databaseSizeBeforeDelete = appUserRepository.findAll().collectList().block().size();

        // Delete the appUser
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, appUser.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
