package com.projet.repository.rowmapper;

import com.projet.domain.Admin;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Admin}, with proper type conversions.
 */
@Service
public class AdminRowMapper implements BiFunction<Row, String, Admin> {

    private final ColumnConverter converter;

    public AdminRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Admin} stored in the database.
     */
    @Override
    public Admin apply(Row row, String prefix) {
        Admin entity = new Admin();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        return entity;
    }
}
