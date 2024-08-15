package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.information.ReaderFromTables;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ResourceBundle;

@Slf4j
public class Main {
    public static void main(String[] args) {
        try {
            ResourceBundle properties = ResourceBundle.getBundle("system");
            ReaderFromTables readerFromTables = new DateBaseConnector();
            log.info("start reading from database");
            var reader = readerFromTables.readFromTables(
                    properties.getString("url"),
                    properties.getString("userName"),
                    properties.getString("password"),
                    properties.getString("driver"));
            log.info("end reading from database");
            reader.writeFieldsToFile("src/resources/file.txt");
            log.info("write info to file");
        } catch (IllegalArgumentException | SQLException | IOException e) {
            log.error(e.getMessage());
        } catch (ClassNotFoundException e) {
            log.error("class of Driver not found");
        }
    }
}