package com.projet.repository;

import com.projet.domain.TypePlante;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TypePlante entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TypePlanteRepository extends ReactiveCrudRepository<TypePlante, Long>, TypePlanteRepositoryInternal {
    @Override
    <S extends TypePlante> Mono<S> save(S entity);

    @Override
    Flux<TypePlante> findAll();

    @Override
    Mono<TypePlante> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TypePlanteRepositoryInternal {
    <S extends TypePlante> Mono<S> save(S entity);

    Flux<TypePlante> findAllBy(Pageable pageable);

    Flux<TypePlante> findAll();

    Mono<TypePlante> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<TypePlante> findAllBy(Pageable pageable, Criteria criteria);
}
