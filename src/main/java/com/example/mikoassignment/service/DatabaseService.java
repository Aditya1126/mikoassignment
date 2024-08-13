package com.example.mikoassignment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class DatabaseService {

    private final Path metadataPath = Paths.get("metadata.txt");
    private final Path tablePath = Paths.get("table.txt");

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    public void saveData(String key, Object data) {
        redisTemplate.opsForValue().set(key, data, 1, TimeUnit.MINUTES);
    }

    public void parseAndExecute(String sql) {
        sql = sql.trim().toUpperCase();

        try {
            if (sql.startsWith("CREATE TABLE")) {
                createTable(sql);
            } else if (sql.startsWith("INSERT INTO")) {
                insertIntoTable(sql);
            } else {
                throw new IllegalArgumentException("Unsupported SQL command.");
            }
            int successCnt=getData("SUCCESS");
            if (successCnt==0) {
                saveData("SUCCESS", 1);
            }else {
                saveData("SUCCESS", successCnt+1);
            }
        } catch (Exception e) {
            int failureCnt=getData("FAILURE");
            if (failureCnt==0) {
                saveData("FAILURE", 1);
            }
            else {
                saveData("FAILURE", failureCnt+1);
            }
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void createTable(String sql) throws IOException {
        String tableDefinition = sql.substring(sql.indexOf("(") + 1, sql.indexOf(")"));
        String tableName = sql.split(" ")[2];

        List<TableMetadata.Column> columns = new ArrayList<>();
        for (String col : tableDefinition.split(",")) {
            String[] parts = col.trim().split(" ");
            columns.add(new TableMetadata.Column(parts[0], parts[1]));
        }

        TableMetadata tableMetadata = new TableMetadata(tableName, columns);
        Files.write(metadataPath, Collections.singletonList(tableMetadata.toString()), StandardOpenOption.WRITE);
    }

    private void insertIntoTable(String sql) throws IOException {
        String valuesPart = sql.substring(sql.indexOf("(")+1 , sql.lastIndexOf(")"));
        String[] values = valuesPart.split(",");
        Files.write(tablePath, Collections.singletonList(String.join(",", values)), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    }

    public int getData(String key) {
        Integer value = (Integer) redisTemplate.opsForValue().get(key);
        return value != null ? value : 0;
    }
}

