package com.projet.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ParcelleSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("libelle", table, columnPrefix + "_libelle"));
        columns.add(Column.aliased("photo", table, columnPrefix + "_photo"));

        columns.add(Column.aliased("ferme_id", table, columnPrefix + "_ferme_id"));
        columns.add(Column.aliased("ferme_libelle_id", table, columnPrefix + "_ferme_libelle_id"));
        return columns;
    }
}
