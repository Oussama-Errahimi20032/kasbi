package com.projet.web.rest;

import com.projet.domain.Ferme;
import com.projet.repository.FermeRepository;
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
 * REST controller for managing {@link com.projet.domain.Ferme}.
 */
@RestController
@RequestMapping("/api/fermes")
@Transactional
public class FermeResource {

    private final Logger log = LoggerFactory.getLogger(FermeResource.class);

    private static final String ENTITY_NAME = "ferme";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FermeRepository fermeRepository;

    public FermeResource(FermeRepository fermeRepository) {
        this.fermeRepository = fermeRepository;
    }

    /**
     * {@code POST  /fermes} : Create a new ferme.
     *
     * @param ferme the ferme to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ferme, or with status {@code 400 (Bad Request)} if the ferme has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Ferme>> createFerme(@RequestBody Ferme ferme) throws URISyntaxException {
        log.debug("REST request to save Ferme : {}", ferme);
        if (ferme.getId() != null) {
            throw new BadRequestAlertException("A new ferme cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return fermeRepository
            .save(ferme)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/fermes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /fermes/:id} : Updates an existing ferme.
     *
     * @param id the id of the ferme to save.
     * @param ferme the ferme to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ferme,
     * or with status {@code 400 (Bad Request)} if the ferme is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ferme couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Ferme>> updateFerme(@PathVariable(value = "id", required = false) final Long id, @RequestBody Ferme ferme)
        throws URISyntaxException {
        log.debug("REST request to update Ferme : {}, {}", id, ferme);
        if (ferme.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ferme.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return fermeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return fermeRepository
                    .save(ferme)
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
     * {@code PATCH  /fermes/:id} : Partial updates given fields of an existing ferme, field will ignore if it is null
     *
     * @param id the id of the ferme to save.
     * @param ferme the ferme to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ferme,
     * or with status {@code 400 (Bad Request)} if the ferme is not valid,
     * or with status {@code 404 (Not Found)} if the ferme is not found,
     * or with status {@code 500 (Internal Server Error)} if the ferme couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Ferme>> partialUpdateFerme(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Ferme ferme
    ) throws URISyntaxException {
        log.debug("REST request to partial update Ferme partially : {}, {}", id, ferme);
        if (ferme.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ferme.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return fermeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Ferme> result = fermeRepository
                    .findById(ferme.getId())
                    .map(existingFerme -> {
                        if (ferme.getLibelle() != null) {
                            existingFerme.setLibelle(ferme.getLibelle());
                        }
                        if (ferme.getPhoto() != null) {
                            existingFerme.setPhoto(ferme.getPhoto());
                        }

                        return existingFerme;
                    })
                    .flatMap(fermeRepository::save);

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
     * {@code GET  /fermes} : get all the fermes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of fermes in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Ferme>> getAllFermes() {
        log.debug("REST request to get all Fermes");
        return fermeRepository.findAll().collectList();
    }

    /**
     * {@code GET  /fermes} : get all the fermes as a stream.
     * @return the {@link Flux} of fermes.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Ferme> getAllFermesAsStream() {
        log.debug("REST request to get all Fermes as a stream");
        return fermeRepository.findAll();
    }

    /**
     * {@code GET  /fermes/:id} : get the "id" ferme.
     *
     * @param id the id of the ferme to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ferme, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Ferme>> getFerme(@PathVariable("id") Long id) {
        log.debug("REST request to get Ferme : {}", id);
        Mono<Ferme> ferme = fermeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(ferme);
    }

    /**
     * {@code DELETE  /fermes/:id} : delete the "id" ferme.
     *
     * @param id the id of the ferme to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteFerme(@PathVariable("id") Long id) {
        log.debug("REST request to delete Ferme : {}", id);
        return fermeRepository
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
