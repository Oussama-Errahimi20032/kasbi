package com.projet.web.rest;

import com.projet.domain.Admin;
import com.projet.repository.AdminRepository;
import com.projet.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.projet.domain.Admin}.
 */
@RestController
@RequestMapping("/api/admins")
@Transactional
public class AdminResource {

    private final Logger log = LoggerFactory.getLogger(AdminResource.class);

    private static final String ENTITY_NAME = "admin";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AdminRepository adminRepository;

    public AdminResource(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    /**
     * {@code POST  /admins} : Create a new admin.
     *
     * @param admin the admin to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new admin, or with status {@code 400 (Bad Request)} if the admin has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Admin>> createAdmin(@RequestBody Admin admin) throws URISyntaxException {
        log.debug("REST request to save Admin : {}", admin);
        if (admin.getId() != null) {
            throw new BadRequestAlertException("A new admin cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return adminRepository
            .save(admin)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/admins/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /admins/:id} : Updates an existing admin.
     *
     * @param id the id of the admin to save.
     * @param admin the admin to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated admin,
     * or with status {@code 400 (Bad Request)} if the admin is not valid,
     * or with status {@code 500 (Internal Server Error)} if the admin couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Admin>> updateAdmin(@PathVariable(value = "id", required = false) final Long id, @RequestBody Admin admin)
        throws URISyntaxException {
        log.debug("REST request to update Admin : {}, {}", id, admin);
        if (admin.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, admin.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return adminRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                // no save call needed as we have no fields that can be updated
                return adminRepository
                    .findById(admin.getId())
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /admins/:id} : Partial updates given fields of an existing admin, field will ignore if it is null
     *
     * @param id the id of the admin to save.
     * @param admin the admin to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated admin,
     * or with status {@code 400 (Bad Request)} if the admin is not valid,
     * or with status {@code 404 (Not Found)} if the admin is not found,
     * or with status {@code 500 (Internal Server Error)} if the admin couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Admin>> partialUpdateAdmin(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Admin admin
    ) throws URISyntaxException {
        log.debug("REST request to partial update Admin partially : {}, {}", id, admin);
        if (admin.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, admin.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return adminRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Admin> result = adminRepository.findById(admin.getId()); // .flatMap(adminRepository::save)

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /admins} : get all the admins.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of admins in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Admin>> getAllAdmins() {
        log.debug("REST request to get all Admins");
        return adminRepository.findAll().collectList();
    }

    /**
     * {@code GET  /admins} : get all the admins as a stream.
     * @return the {@link Flux} of admins.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Admin> getAllAdminsAsStream() {
        log.debug("REST request to get all Admins as a stream");
        return adminRepository.findAll();
    }

    /**
     * {@code GET  /admins/:id} : get the "id" admin.
     *
     * @param id the id of the admin to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the admin, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Admin>> getAdmin(@PathVariable("id") Long id) {
        log.debug("REST request to get Admin : {}", id);
        Mono<Admin> admin = adminRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(admin);
    }

    /**
     * {@code DELETE  /admins/:id} : delete the "id" admin.
     *
     * @param id the id of the admin to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAdmin(@PathVariable("id") Long id) {
        log.debug("REST request to delete Admin : {}", id);
        return adminRepository
            .deleteById(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
