package com.narryel.fitness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.telegram.telegrambots.ApiContextInitializer;


@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class FitnessApplication {

    public static void main(String[] args) {

        ApiContextInitializer.init(); // hack
        SpringApplication.run(FitnessApplication.class, args);
    }

}
