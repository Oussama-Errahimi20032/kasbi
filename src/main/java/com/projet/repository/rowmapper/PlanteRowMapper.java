package com.projet.repository.rowmapper;

import com.projet.domain.Plante;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Plante}, with proper type conversions.
 */
@Service
public class PlanteRowMapper implements BiFunction<Row, String, Plante> {

    private final ColumnConverter converter;

    public PlanteRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Plante} stored in the database.
     */
    @Override
    public Plante apply(Row row, String prefix) {
        Plante entity = new Plante();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLibelle(converter.fromRow(row, prefix + "_libelle", String.class));
        entity.setRacine(converter.fromRow(row, prefix + "_racine", String.class));
        entity.setImage(converter.fromRow(row, prefix + "_image", String.class));
        entity.setTypePlanteId(converter.fromRow(row, prefix + "_type_plante_id", Long.class));
        entity.setNomId(converter.fromRow(row, prefix + "_nom_id", Long.class));
        return entity;
    }
}
