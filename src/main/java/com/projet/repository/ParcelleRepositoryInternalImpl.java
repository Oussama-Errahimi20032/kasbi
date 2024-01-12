package com.projet.repository;

import com.projet.domain.Parcelle;
import com.projet.domain.Plante;
import com.projet.repository.rowmapper.FermeRowMapper;
import com.projet.repository.rowmapper.ParcelleRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Parcelle entity.
 */
@SuppressWarnings("unused")
class ParcelleRepositoryInternalImpl extends SimpleR2dbcRepository<Parcelle, Long> implements ParcelleRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final FermeRowMapper fermeMapper;
    private final ParcelleRowMapper parcelleMapper;

    private static final Table entityTable = Table.aliased("parcelle", EntityManager.ENTITY_ALIAS);
    private static final Table fermeTable = Table.aliased("ferme", "ferme");
    private static final Table fermeLibelleTable = Table.aliased("ferme", "fermeLibelle");

    private static final EntityManager.LinkTable plantesLink = new EntityManager.LinkTable(
        "rel_parcelle__plantes",
        "parcelle_id",
        "plantes_id"
    );

    public ParcelleRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        FermeRowMapper fermeMapper,
        ParcelleRowMapper parcelleMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Parcelle.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.fermeMapper = fermeMapper;
        this.parcelleMapper = parcelleMapper;
    }

    @Override
    public Flux<Parcelle> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Parcelle> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ParcelleSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(FermeSqlHelper.getColumns(fermeTable, "ferme"));
        columns.addAll(FermeSqlHelper.getColumns(fermeLibelleTable, "fermeLibelle"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(fermeTable)
            .on(Column.create("ferme_id", entityTable))
            .equals(Column.create("id", fermeTable))
            .leftOuterJoin(fermeLibelleTable)
            .on(Column.create("ferme_libelle_id", entityTable))
            .equals(Column.create("id", fermeLibelleTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Parcelle.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Parcelle> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Parcelle> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Parcelle> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Parcelle> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Parcelle> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Parcelle process(Row row, RowMetadata metadata) {
        Parcelle entity = parcelleMapper.apply(row, "e");
        entity.setFerme(fermeMapper.apply(row, "ferme"));
        entity.setFermeLibelle(fermeMapper.apply(row, "fermeLibelle"));
        return entity;
    }

    @Override
    public <S extends Parcelle> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends Parcelle> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(plantesLink, entity.getId(), entity.getPlantes().stream().map(Plante::getId))
            .then();
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(plantesLink, entityId);
    }
}
