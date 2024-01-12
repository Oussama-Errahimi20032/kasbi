package com.projet.repository.rowmapper;

import com.projet.domain.Parcelle;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Parcelle}, with proper type conversions.
 */
@Service
public class ParcelleRowMapper implements BiFunction<Row, String, Parcelle> {

    private final ColumnConverter converter;

    public ParcelleRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Parcelle} stored in the database.
     */
    @Override
    public Parcelle apply(Row row, String prefix) {
        Parcelle entity = new Parcelle();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLibelle(converter.fromRow(row, prefix + "_libelle", String.class));
        entity.setPhoto(converter.fromRow(row, prefix + "_photo", String.class));
        entity.setFermeId(converter.fromRow(row, prefix + "_ferme_id", Long.class));
        entity.setFermeLibelleId(converter.fromRow(row, prefix + "_ferme_libelle_id", Long.class));
        return entity;
    }
}
