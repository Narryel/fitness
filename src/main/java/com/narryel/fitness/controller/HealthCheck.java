package com.narryel.fitness.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/health")
@Slf4j
public class HealthCheck {

    @GetMapping
    public String checkAvailability(){
        log.info("healthCheck all is good");
        return "Bot is feeling okay, beep beep bop";
    }
}
