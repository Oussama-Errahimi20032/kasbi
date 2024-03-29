package com.projet.repository;

import com.projet.domain.Plante;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Plante entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlanteRepository extends ReactiveCrudRepository<Plante, Long>, PlanteRepositoryInternal {
    @Query("SELECT * FROM plante entity WHERE entity.type_plante_id = :id")
    Flux<Plante> findByTypePlante(Long id);

    @Query("SELECT * FROM plante entity WHERE entity.type_plante_id IS NULL")
    Flux<Plante> findAllWhereTypePlanteIsNull();

    @Query("SELECT * FROM plante entity WHERE entity.nom_id = :id")
    Flux<Plante> findByNom(Long id);

    @Query("SELECT * FROM plante entity WHERE entity.nom_id IS NULL")
    Flux<Plante> findAllWhereNomIsNull();

    @Override
    <S extends Plante> Mono<S> save(S entity);

    @Override
    Flux<Plante> findAll();

    @Override
    Mono<Plante> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PlanteRepositoryInternal {
    <S extends Plante> Mono<S> save(S entity);

    Flux<Plante> findAllBy(Pageable pageable);

    Flux<Plante> findAll();

    Mono<Plante> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Plante> findAllBy(Pageable pageable, Criteria criteria);
}
