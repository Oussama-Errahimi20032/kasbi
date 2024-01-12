package com.projet.repository;

import com.projet.domain.Plantage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Plantage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlantageRepository extends ReactiveCrudRepository<Plantage, Long>, PlantageRepositoryInternal {
    @Query("SELECT * FROM plantage entity WHERE entity.plante_libelle_id = :id")
    Flux<Plantage> findByPlanteLibelle(Long id);

    @Query("SELECT * FROM plantage entity WHERE entity.plante_libelle_id IS NULL")
    Flux<Plantage> findAllWherePlanteLibelleIsNull();

    @Query("SELECT * FROM plantage entity WHERE entity.parcelle_libelle_id = :id")
    Flux<Plantage> findByParcelleLibelle(Long id);

    @Query("SELECT * FROM plantage entity WHERE entity.parcelle_libelle_id IS NULL")
    Flux<Plantage> findAllWhereParcelleLibelleIsNull();

    @Override
    <S extends Plantage> Mono<S> save(S entity);

    @Override
    Flux<Plantage> findAll();

    @Override
    Mono<Plantage> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PlantageRepositoryInternal {
    <S extends Plantage> Mono<S> save(S entity);

    Flux<Plantage> findAllBy(Pageable pageable);

    Flux<Plantage> findAll();

    Mono<Plantage> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Plantage> findAllBy(Pageable pageable, Criteria criteria);
}
