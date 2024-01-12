package com.projet.web.rest;

import com.projet.domain.AppUser;
import com.projet.repository.AppUserRepository;
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
 * REST controller for managing {@link com.projet.domain.AppUser}.
 */
@RestController
@RequestMapping("/api/app-users")
@Transactional
public class AppUserResource {

    private final Logger log = LoggerFactory.getLogger(AppUserResource.class);

    private static final String ENTITY_NAME = "appUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AppUserRepository appUserRepository;

    public AppUserResource(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    /**
     * {@code POST  /app-users} : Create a new appUser.
     *
     * @param appUser the appUser to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new appUser, or with status {@code 400 (Bad Request)} if the appUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<AppUser>> createAppUser(@RequestBody AppUser appUser) throws URISyntaxException {
        log.debug("REST request to save AppUser : {}", appUser);
        if (appUser.getId() != null) {
            throw new BadRequestAlertException("A new appUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return appUserRepository
            .save(appUser)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/app-users/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /app-users/:id} : Updates an existing appUser.
     *
     * @param id the id of the appUser to save.
     * @param appUser the appUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appUser,
     * or with status {@code 400 (Bad Request)} if the appUser is not valid,
     * or with status {@code 500 (Internal Server Error)} if the appUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<AppUser>> updateAppUser(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AppUser appUser
    ) throws URISyntaxException {
        log.debug("REST request to update AppUser : {}, {}", id, appUser);
        if (appUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return appUserRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return appUserRepository
                    .save(appUser)
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
     * {@code PATCH  /app-users/:id} : Partial updates given fields of an existing appUser, field will ignore if it is null
     *
     * @param id the id of the appUser to save.
     * @param appUser the appUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appUser,
     * or with status {@code 400 (Bad Request)} if the appUser is not valid,
     * or with status {@code 404 (Not Found)} if the appUser is not found,
     * or with status {@code 500 (Internal Server Error)} if the appUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<AppUser>> partialUpdateAppUser(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AppUser appUser
    ) throws URISyntaxException {
        log.debug("REST request to partial update AppUser partially : {}, {}", id, appUser);
        if (appUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return appUserRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<AppUser> result = appUserRepository
                    .findById(appUser.getId())
                    .map(existingAppUser -> {
                        if (appUser.getUsername() != null) {
                            existingAppUser.setUsername(appUser.getUsername());
                        }
                        if (appUser.getEmail() != null) {
                            existingAppUser.setEmail(appUser.getEmail());
                        }
                        if (appUser.getPassword() != null) {
                            existingAppUser.setPassword(appUser.getPassword());
                        }
                        if (appUser.getAddress() != null) {
                            existingAppUser.setAddress(appUser.getAddress());
                        }
                        if (appUser.getPhone() != null) {
                            existingAppUser.setPhone(appUser.getPhone());
                        }
                        if (appUser.getRole() != null) {
                            existingAppUser.setRole(appUser.getRole());
                        }
                        if (appUser.getImage() != null) {
                            existingAppUser.setImage(appUser.getImage());
                        }

                        return existingAppUser;
                    })
                    .flatMap(appUserRepository::save);

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
     * {@code GET  /app-users} : get all the appUsers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of appUsers in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<AppUser>> getAllAppUsers() {
        log.debug("REST request to get all AppUsers");
        return appUserRepository.findAll().collectList();
    }

    /**
     * {@code GET  /app-users} : get all the appUsers as a stream.
     * @return the {@link Flux} of appUsers.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<AppUser> getAllAppUsersAsStream() {
        log.debug("REST request to get all AppUsers as a stream");
        return appUserRepository.findAll();
    }

    /**
     * {@code GET  /app-users/:id} : get the "id" appUser.
     *
     * @param id the id of the appUser to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the appUser, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AppUser>> getAppUser(@PathVariable("id") Long id) {
        log.debug("REST request to get AppUser : {}", id);
        Mono<AppUser> appUser = appUserRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(appUser);
    }

    /**
     * {@code DELETE  /app-users/:id} : delete the "id" appUser.
     *
     * @param id the id of the appUser to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAppUser(@PathVariable("id") Long id) {
        log.debug("REST request to delete AppUser : {}", id);
        return appUserRepository
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
