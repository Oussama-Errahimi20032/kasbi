package com.projet.repository;

import com.projet.domain.Plantage;
import com.projet.repository.rowmapper.ParcelleRowMapper;
import com.projet.repository.rowmapper.PlantageRowMapper;
import com.projet.repository.rowmapper.PlanteRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Plantage entity.
 */
@SuppressWarnings("unused")
class PlantageRepositoryInternalImpl extends SimpleR2dbcRepository<Plantage, Long> implements PlantageRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final PlanteRowMapper planteMapper;
    private final ParcelleRowMapper parcelleMapper;
    private final PlantageRowMapper plantageMapper;

    private static final Table entityTable = Table.aliased("plantage", EntityManager.ENTITY_ALIAS);
    private static final Table planteLibelleTable = Table.aliased("plante", "planteLibelle");
    private static final Table parcelleLibelleTable = Table.aliased("parcelle", "parcelleLibelle");

    public PlantageRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        PlanteRowMapper planteMapper,
        ParcelleRowMapper parcelleMapper,
        PlantageRowMapper plantageMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Plantage.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.planteMapper = planteMapper;
        this.parcelleMapper = parcelleMapper;
        this.plantageMapper = plantageMapper;
    }

    @Override
    public Flux<Plantage> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Plantage> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = PlantageSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(PlanteSqlHelper.getColumns(planteLibelleTable, "planteLibelle"));
        columns.addAll(ParcelleSqlHelper.getColumns(parcelleLibelleTable, "parcelleLibelle"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(planteLibelleTable)
            .on(Column.create("plante_libelle_id", entityTable))
            .equals(Column.create("id", planteLibelleTable))
            .leftOuterJoin(parcelleLibelleTable)
            .on(Column.create("parcelle_libelle_id", entityTable))
            .equals(Column.create("id", parcelleLibelleTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Plantage.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Plantage> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Plantage> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Plantage process(Row row, RowMetadata metadata) {
        Plantage entity = plantageMapper.apply(row, "e");
        entity.setPlanteLibelle(planteMapper.apply(row, "planteLibelle"));
        entity.setParcelleLibelle(parcelleMapper.apply(row, "parcelleLibelle"));
        return entity;
    }

    @Override
    public <S extends Plantage> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
