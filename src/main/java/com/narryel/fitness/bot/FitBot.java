package com.narryel.fitness.bot;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Data
@Slf4j
@Component
public class FitBot extends TelegramLongPollingBot {

    @Value("${bot.longpolling.credentials.username}")
    private String userName;

    @Value("${bot.longpolling.credentials.token}")
    private String token;

    private static final String START_CMD = "/start";

    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (START_CMD.equals(update.getMessage().getText())) {
                handleStart(update);
                return;
            }


            SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                    .setChatId(update.getMessage().getChatId())
                    .setText(update.getMessage().getText());
            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }
    }

    @SneakyThrows
    private void handleStart(Update update) {
        final var inlineKeyboardMarkup = new InlineKeyboardMarkup();

        final var button1 = new InlineKeyboardButton();
        button1.setText("Зарегистрироваться");
        button1.setCallbackData("/registerUser");

        final var button2 = new InlineKeyboardButton();
        button2.setText("кто я");
        button2.setCallbackData("/me");

        inlineKeyboardMarkup.setKeyboard(List.of(List.of(button1,button2)));

        var message = new SendMessage() // Create a SendMessage object with mandatory fields
                .setChatId(update.getMessage().getChatId())
                .setText("че делаем")
                .setReplyMarkup(inlineKeyboardMarkup);


        execute(message);





    }

    @Override
    public String getBotUsername() {
        return userName;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
