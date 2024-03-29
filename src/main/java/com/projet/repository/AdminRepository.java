package com.projet.repository;

import com.projet.domain.Admin;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Admin entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AdminRepository extends ReactiveCrudRepository<Admin, Long>, AdminRepositoryInternal {
    @Override
    <S extends Admin> Mono<S> save(S entity);

    @Override
    Flux<Admin> findAll();

    @Override
    Mono<Admin> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AdminRepositoryInternal {
    <S extends Admin> Mono<S> save(S entity);

    Flux<Admin> findAllBy(Pageable pageable);

    Flux<Admin> findAll();

    Mono<Admin> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Admin> findAllBy(Pageable pageable, Criteria criteria);
}
