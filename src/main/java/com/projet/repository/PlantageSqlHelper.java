package com.projet.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class PlantageSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("date", table, columnPrefix + "_date"));
        columns.add(Column.aliased("nombre", table, columnPrefix + "_nombre"));

        columns.add(Column.aliased("plante_libelle_id", table, columnPrefix + "_plante_libelle_id"));
        columns.add(Column.aliased("parcelle_libelle_id", table, columnPrefix + "_parcelle_libelle_id"));
        return columns;
    }
}
