package com.projet.repository;

import com.projet.domain.Parcelle;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Parcelle entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ParcelleRepository extends ReactiveCrudRepository<Parcelle, Long>, ParcelleRepositoryInternal {
    @Override
    Mono<Parcelle> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Parcelle> findAllWithEagerRelationships();

    @Override
    Flux<Parcelle> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM parcelle entity WHERE entity.ferme_id = :id")
    Flux<Parcelle> findByFerme(Long id);

    @Query("SELECT * FROM parcelle entity WHERE entity.ferme_id IS NULL")
    Flux<Parcelle> findAllWhereFermeIsNull();

    @Query(
        "SELECT entity.* FROM parcelle entity JOIN rel_parcelle__plantes joinTable ON entity.id = joinTable.plantes_id WHERE joinTable.plantes_id = :id"
    )
    Flux<Parcelle> findByPlantes(Long id);

    @Query("SELECT * FROM parcelle entity WHERE entity.ferme_libelle_id = :id")
    Flux<Parcelle> findByFermeLibelle(Long id);

    @Query("SELECT * FROM parcelle entity WHERE entity.ferme_libelle_id IS NULL")
    Flux<Parcelle> findAllWhereFermeLibelleIsNull();

    @Override
    <S extends Parcelle> Mono<S> save(S entity);

    @Override
    Flux<Parcelle> findAll();

    @Override
    Mono<Parcelle> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ParcelleRepositoryInternal {
    <S extends Parcelle> Mono<S> save(S entity);

    Flux<Parcelle> findAllBy(Pageable pageable);

    Flux<Parcelle> findAll();

    Mono<Parcelle> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Parcelle> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Parcelle> findOneWithEagerRelationships(Long id);

    Flux<Parcelle> findAllWithEagerRelationships();

    Flux<Parcelle> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
