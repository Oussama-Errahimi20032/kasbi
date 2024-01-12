package com.projet.repository;

import com.projet.domain.Ferme;
import com.projet.repository.rowmapper.AppUserRowMapper;
import com.projet.repository.rowmapper.FermeRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Ferme entity.
 */
@SuppressWarnings("unused")
class FermeRepositoryInternalImpl extends SimpleR2dbcRepository<Ferme, Long> implements FermeRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final AppUserRowMapper appuserMapper;
    private final FermeRowMapper fermeMapper;

    private static final Table entityTable = Table.aliased("ferme", EntityManager.ENTITY_ALIAS);
    private static final Table usersTable = Table.aliased("app_user", "users");
    private static final Table appUserTable = Table.aliased("app_user", "appUser");

    public FermeRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        AppUserRowMapper appuserMapper,
        FermeRowMapper fermeMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Ferme.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.appuserMapper = appuserMapper;
        this.fermeMapper = fermeMapper;
    }

    @Override
    public Flux<Ferme> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Ferme> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = FermeSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(AppUserSqlHelper.getColumns(usersTable, "users"));
        columns.addAll(AppUserSqlHelper.getColumns(appUserTable, "appUser"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(usersTable)
            .on(Column.create("users_id", entityTable))
            .equals(Column.create("id", usersTable))
            .leftOuterJoin(appUserTable)
            .on(Column.create("app_user_id", entityTable))
            .equals(Column.create("id", appUserTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Ferme.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Ferme> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Ferme> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Ferme process(Row row, RowMetadata metadata) {
        Ferme entity = fermeMapper.apply(row, "e");
        entity.setUsers(appuserMapper.apply(row, "users"));
        entity.setAppUser(appuserMapper.apply(row, "appUser"));
        return entity;
    }

    @Override
    public <S extends Ferme> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
