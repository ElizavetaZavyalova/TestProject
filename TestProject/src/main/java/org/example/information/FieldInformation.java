package org.example.information;

import lombok.*;


@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class FieldInformation {
    final String tableName;
    final String fieldName;
    String fieldType;

    public String getWriteInfo() {
        return new StringBuilder().append(tableName).append(", ")
                .append(fieldName).append(", ")
                .append(fieldType).append("\n").toString();
    }

    public boolean isFieldTypePresent() {
        return fieldType != null;
    }
}
