package com.narryel.fitness.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("bot.ability.credentials")
public class AbilityBotCredentials {
    private String userName;

    private String token;

    private Integer creatorId;
}
