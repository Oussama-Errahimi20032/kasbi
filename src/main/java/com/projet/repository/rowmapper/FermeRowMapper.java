package com.projet.repository.rowmapper;

import com.projet.domain.Ferme;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Ferme}, with proper type conversions.
 */
@Service
public class FermeRowMapper implements BiFunction<Row, String, Ferme> {

    private final ColumnConverter converter;

    public FermeRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Ferme} stored in the database.
     */
    @Override
    public Ferme apply(Row row, String prefix) {
        Ferme entity = new Ferme();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLibelle(converter.fromRow(row, prefix + "_libelle", String.class));
        entity.setPhoto(converter.fromRow(row, prefix + "_photo", String.class));
        entity.setUsersId(converter.fromRow(row, prefix + "_users_id", Long.class));
        entity.setAppUserId(converter.fromRow(row, prefix + "_app_user_id", Long.class));
        return entity;
    }
}
