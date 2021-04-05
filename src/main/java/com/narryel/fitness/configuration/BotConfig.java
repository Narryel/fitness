package com.narryel.fitness.configuration;

import com.narryel.fitness.bot.FitAbilityBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotConfig {

    private final FitAbilityBot fitAbilityBot;

    @PostConstruct
    private void init() throws TelegramApiException {
        registerBot(List.of(fitAbilityBot));
    }

    private void registerBot(List<LongPollingBot> bots) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            for (LongPollingBot bot : bots) {
                telegramBotsApi.registerBot(bot);
            }
        } catch (TelegramApiException e) {
            log.error("Exception was thrown when registering bot/bots. Ex.message = {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
