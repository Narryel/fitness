package com.narryel.fitness.bot.handlers.input;

import com.narryel.fitness.domain.enums.State;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UserInputHandler {
    SendMessage handle(Update update);

    State stateToHandle();

    default Long getChatId(Update update) {
        return update.getMessage().getChatId();
    }
    default String getText(Update update) {
        return update.getMessage().getText();
    }
}
