package org.example;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.example.information.FieldInformation;
import org.example.information.ReaderFromTables;

import java.sql.*;
import java.util.Arrays;
import java.util.Optional;

import static org.example.DateBaseConnector.QueryConstants.*;
import static org.example.DateBaseConnector.QueryConstants.TableColsConstants.*;
import static org.example.DateBaseConnector.QueryConstants.TableListConstants.*;

@Slf4j
public class DateBaseConnector implements ReaderFromTables {

    @UtilityClass
    static class QueryConstants {
        public static final String QUERY = "SELECT * FROM ";
        public static final String CORRECT_NAME_REG = "^[a-zA-Z_]\\w*$";

        @UtilityClass
        static class TableListConstants {
            public static final String TABLE_LIST = "table_list";
            public static final String TABLE_NAME_OF_LIST = "table_name";
            public static final String PK = "pk";
            public static final String PKS_SPLIT_REG = ",";
        }

        @UtilityClass
        static class TableColsConstants {
            public static final String TABLE_COLS = "table_cols";
            public static final String TABLE_NAME_OF_COLS = "table_name";
            public static final String COLUMN_NAME = "column_name";
            public static final String COLUMN_TYPE = "column_type";
        }
    }

    @Override
    public FieldsInformation readFromTables(String jdbcUrl, String user,
                                            String password, String driver)
            throws SQLException, IllegalArgumentException, ClassNotFoundException {
        Class.forName(driver);
        log.debug("url:{} user:{} pass:{}", jdbcUrl, user, password);
        try (var connection = DriverManager.getConnection(jdbcUrl, user, password)) {
            return readFromTablesAndParseDate(connection);
        }
    }

    FieldsInformation readFromTablesAndParseDate(Connection connection) throws SQLException {
        FieldsInformation fieldsInformation = new FieldsInformation();
        try (var resultSetOfTableList = connection.createStatement()
                .executeQuery(String.format("%s%s;", QUERY, TABLE_LIST))) {
            fieldsInformation = parseTableList(fieldsInformation, resultSetOfTableList);
        }
        try (var resultSetOfTableCols = connection.createStatement()
                .executeQuery(String.format("%s%s;", QUERY, TABLE_COLS))) {
            fieldsInformation = parseTableCols(fieldsInformation, resultSetOfTableCols);
        }
        fieldsInformation.validateType();
        return fieldsInformation;
    }


    FieldsInformation parseTableCols(FieldsInformation fieldsInformation,
                                     ResultSet resultSet)
            throws SQLException, IllegalArgumentException {
        while (resultSet.next()) {
            String tableName = resultSet.getString(TABLE_NAME_OF_LIST);
            validateAndReturnName(tableName, TABLE_NAME_OF_LIST);
            String columnName = resultSet.getString(COLUMN_NAME);
            validateAndReturnName(columnName, COLUMN_NAME);
            String columnType = resultSet.getString(COLUMN_TYPE);
            validateType(columnType, COLUMN_TYPE);
            fieldsInformation.setTypeOfFieldInformation(tableName, columnName, columnType);
        }
        return fieldsInformation;
    }

    void validateType(String name, String columnType)
            throws IllegalArgumentException {
        log.debug("Validating {}", name);
        if (Optional.ofNullable(name).orElse("").isEmpty()) {
            throw new IllegalArgumentException(columnType + " must not be empty");
        }
    }

    String validateAndReturnName(String name, String fieldName)
            throws IllegalArgumentException {
        if (!Optional.ofNullable(name).orElse("").matches(CORRECT_NAME_REG)) {
            throw new IllegalArgumentException(fieldName + " is not a valid correct name");
        }
        return name;
    }

    FieldsInformation parseTableList(FieldsInformation fieldsInformation,
                                     ResultSet resultSet)
            throws SQLException, IllegalArgumentException {
        while (resultSet.next()) {
            String tableName = resultSet.getString(TABLE_NAME_OF_COLS);
            validateAndReturnName(tableName, TABLE_NAME_OF_COLS);
            String pks = Optional.ofNullable(resultSet.getString(PK)).orElse("");
            Arrays.stream(pks.split(PKS_SPLIT_REG))
                    .forEach(pk -> fieldsInformation.addField(
                            new FieldInformation(tableName,
                                    validateAndReturnName(pk, PK))));
        }
        return fieldsInformation;
    }
}
