package com.projet.repository.rowmapper;

import com.projet.domain.TypePlante;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TypePlante}, with proper type conversions.
 */
@Service
public class TypePlanteRowMapper implements BiFunction<Row, String, TypePlante> {

    private final ColumnConverter converter;

    public TypePlanteRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TypePlante} stored in the database.
     */
    @Override
    public TypePlante apply(Row row, String prefix) {
        TypePlante entity = new TypePlante();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setHumiditeMax(converter.fromRow(row, prefix + "_humidite_max", Integer.class));
        entity.setHumiditeMin(converter.fromRow(row, prefix + "_humidite_min", Integer.class));
        entity.setTemperature(converter.fromRow(row, prefix + "_temperature", Integer.class));
        return entity;
    }
}
