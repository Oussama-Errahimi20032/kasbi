package com.projet.repository;

import com.projet.domain.AppUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the AppUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AppUserRepository extends ReactiveCrudRepository<AppUser, Long>, AppUserRepositoryInternal {
    @Override
    <S extends AppUser> Mono<S> save(S entity);

    @Override
    Flux<AppUser> findAll();

    @Override
    Mono<AppUser> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AppUserRepositoryInternal {
    <S extends AppUser> Mono<S> save(S entity);

    Flux<AppUser> findAllBy(Pageable pageable);

    Flux<AppUser> findAll();

    Mono<AppUser> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<AppUser> findAllBy(Pageable pageable, Criteria criteria);
}
