package com.projet.repository.rowmapper;

import com.projet.domain.AppUser;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link AppUser}, with proper type conversions.
 */
@Service
public class AppUserRowMapper implements BiFunction<Row, String, AppUser> {

    private final ColumnConverter converter;

    public AppUserRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link AppUser} stored in the database.
     */
    @Override
    public AppUser apply(Row row, String prefix) {
        AppUser entity = new AppUser();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setUsername(converter.fromRow(row, prefix + "_username", String.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setPassword(converter.fromRow(row, prefix + "_password", String.class));
        entity.setAddress(converter.fromRow(row, prefix + "_address", String.class));
        entity.setPhone(converter.fromRow(row, prefix + "_phone", String.class));
        entity.setRole(converter.fromRow(row, prefix + "_role", String.class));
        entity.setImage(converter.fromRow(row, prefix + "_image", String.class));
        return entity;
    }
}
