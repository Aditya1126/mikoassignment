package com.example.mikoassignment.service;

import java.util.List;

public class TableMetadata {
    private String tableName;
    private List<Column> columns;

    public TableMetadata(String tableName, List<Column> columns) {
        this.tableName = tableName;
        this.columns = columns;
    }

    public static class Column {
        private String name;
        private String type;

        public Column(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(tableName).append(":");
        for (Column column : columns) {
            builder.append(column.name).append(" ").append(column.type).append(",");
        }
        return builder.toString();
    }
}
