package com.projet.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class PlanteSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("libelle", table, columnPrefix + "_libelle"));
        columns.add(Column.aliased("racine", table, columnPrefix + "_racine"));
        columns.add(Column.aliased("image", table, columnPrefix + "_image"));

        columns.add(Column.aliased("type_plante_id", table, columnPrefix + "_type_plante_id"));
        columns.add(Column.aliased("nom_id", table, columnPrefix + "_nom_id"));
        return columns;
    }
}
