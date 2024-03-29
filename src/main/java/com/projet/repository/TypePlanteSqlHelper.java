package com.projet.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class TypePlanteSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("humidite_max", table, columnPrefix + "_humidite_max"));
        columns.add(Column.aliased("humidite_min", table, columnPrefix + "_humidite_min"));
        columns.add(Column.aliased("temperature", table, columnPrefix + "_temperature"));

        return columns;
    }
}
