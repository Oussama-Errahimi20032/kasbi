package com.projet.repository;

import com.projet.domain.Plante;
import com.projet.repository.rowmapper.PlanteRowMapper;
import com.projet.repository.rowmapper.TypePlanteRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Plante entity.
 */
@SuppressWarnings("unused")
class PlanteRepositoryInternalImpl extends SimpleR2dbcRepository<Plante, Long> implements PlanteRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TypePlanteRowMapper typeplanteMapper;
    private final PlanteRowMapper planteMapper;

    private static final Table entityTable = Table.aliased("plante", EntityManager.ENTITY_ALIAS);
    private static final Table typePlanteTable = Table.aliased("type_plante", "typePlante");
    private static final Table nomTable = Table.aliased("type_plante", "nom");

    public PlanteRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TypePlanteRowMapper typeplanteMapper,
        PlanteRowMapper planteMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Plante.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.typeplanteMapper = typeplanteMapper;
        this.planteMapper = planteMapper;
    }

    @Override
    public Flux<Plante> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Plante> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = PlanteSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(TypePlanteSqlHelper.getColumns(typePlanteTable, "typePlante"));
        columns.addAll(TypePlanteSqlHelper.getColumns(nomTable, "nom"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(typePlanteTable)
            .on(Column.create("type_plante_id", entityTable))
            .equals(Column.create("id", typePlanteTable))
            .leftOuterJoin(nomTable)
            .on(Column.create("nom_id", entityTable))
            .equals(Column.create("id", nomTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Plante.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Plante> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Plante> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Plante process(Row row, RowMetadata metadata) {
        Plante entity = planteMapper.apply(row, "e");
        entity.setTypePlante(typeplanteMapper.apply(row, "typePlante"));
        entity.setNom(typeplanteMapper.apply(row, "nom"));
        return entity;
    }

    @Override
    public <S extends Plante> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
