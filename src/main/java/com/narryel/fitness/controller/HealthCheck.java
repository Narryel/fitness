package com.narryel.fitness.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/health")
public class HealthCheck {

    @GetMapping
    public String checkAvailability(){
        return "Bot is feeling okay, beep beep bop";
    }
}
