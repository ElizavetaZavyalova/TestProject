package org.example.information;


import org.example.FieldsInformation;

import java.sql.SQLException;

@FunctionalInterface
public interface ReaderFromTables {
    FieldsInformation readFromTables(String jdbcUrl, String user, String password, String driver) throws SQLException, ClassNotFoundException;
}
