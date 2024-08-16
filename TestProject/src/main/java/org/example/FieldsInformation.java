package org.example;

import lombok.ToString;
import org.example.information.FieldInformation;


import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;



@ToString
public class FieldsInformation {
    HashMap<String, FieldInformation> information = new HashMap<>();

    public void writeFieldsToFile(String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (FieldInformation field : information.values()) {
                writer.write(field.getWriteInfo());
            }
        }
    }

    public void validateType() throws IllegalArgumentException {
        for (FieldInformation field : information.values()) {
            if (!field.isFieldTypePresent()) {
                throw new IllegalArgumentException("no type of :" +
                        field.getTableName() + " " + field.getFieldName());
            }
        }
    }

    void addField(FieldInformation field) throws IllegalArgumentException {
        String key=makeKey(field.getTableName(), field.getFieldName());
        if(!information.containsKey(key)){
            information.put(makeKey(field.getTableName(), field.getFieldName()), field);
            return;
        }
        throw new IllegalArgumentException("field info is deduplicate");
    }

    private String makeKey(String tableName, String fieldName) {
        return (tableName + '+' + fieldName).toUpperCase();
    }

    void setTypeOfFieldInformation(String tableName, String fieldName, String fieldType)
            throws IllegalArgumentException {
        String key = makeKey(tableName, fieldName);
        if (information.containsKey(key)) {
            var field = information.get(key);
            if(!field.isFieldTypePresent()) {
                field.setFieldType(fieldType);
                return;
            }
            throw new IllegalArgumentException("field Type is duplicate");
        }
    }
}
