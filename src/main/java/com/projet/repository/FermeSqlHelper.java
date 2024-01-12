package com.projet.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class FermeSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("libelle", table, columnPrefix + "_libelle"));
        columns.add(Column.aliased("photo", table, columnPrefix + "_photo"));

        columns.add(Column.aliased("users_id", table, columnPrefix + "_users_id"));
        columns.add(Column.aliased("app_user_id", table, columnPrefix + "_app_user_id"));
        return columns;
    }
}
