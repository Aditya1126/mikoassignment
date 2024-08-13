package com.example.mikoassignment.controller;


import com.example.mikoassignment.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DatabaseController {

    @Autowired
    private DatabaseService databaseService;

    @PostMapping("/execute")
    public String executeSql(@RequestBody String sql) {
        try {
            databaseService.parseAndExecute(sql);
            return "Success";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}

