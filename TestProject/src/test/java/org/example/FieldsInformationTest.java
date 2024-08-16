package org.example;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class FieldsInformationTest {
    static final String DATE_BASE_CONNECTOR = "jdbc:h2:mem:default";
    static final String USER = "";
    static final String PASSWORD = "";
    DateBaseConnector connector;
    Connection connection;

    void setUp(String settingsFile) throws SQLException, IOException {
        connector = new DateBaseConnector();
        connection = DriverManager.getConnection(DATE_BASE_CONNECTOR, USER, PASSWORD);
        String fileOfSql = Files.lines(Paths.get(settingsFile))
                .collect(Collectors.joining(" "));
        var statement = connection.createStatement();
        statement.execute(fileOfSql);
        statement.close();
        log.info(connection.toString());
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void writeFieldsToFile() throws SQLException, IOException {
        setUp("src/test/resources/init.sql");
        var result = connector.readFromTablesAndParseDate(connection);
        assertNotNull(result);
        result.writeFieldsToFile("src/test/resources/file.txt");
    }


}