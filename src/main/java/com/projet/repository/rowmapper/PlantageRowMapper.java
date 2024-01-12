package com.projet.repository.rowmapper;

import com.projet.domain.Plantage;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Plantage}, with proper type conversions.
 */
@Service
public class PlantageRowMapper implements BiFunction<Row, String, Plantage> {

    private final ColumnConverter converter;

    public PlantageRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Plantage} stored in the database.
     */
    @Override
    public Plantage apply(Row row, String prefix) {
        Plantage entity = new Plantage();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDate(converter.fromRow(row, prefix + "_date", LocalDate.class));
        entity.setNombre(converter.fromRow(row, prefix + "_nombre", Integer.class));
        entity.setPlanteLibelleId(converter.fromRow(row, prefix + "_plante_libelle_id", Long.class));
        entity.setParcelleLibelleId(converter.fromRow(row, prefix + "_parcelle_libelle_id", Long.class));
        return entity;
    }
}
