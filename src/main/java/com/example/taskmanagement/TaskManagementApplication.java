package com.example.taskmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagementApplication.class, args);

        System.out.println("NEW CODE DEPLOYED THROUGH JENKINS");
    }

    // Jenkins webhook test
}