package com.narryel.fitness.bot.handlers.input;

import com.narryel.fitness.bot.handlers.input.validation.ValidationResult;
import com.narryel.fitness.domain.enums.State;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UserInputHandler {
    SendMessage handle(Update update);

    State stateToHandle();

    /**
     * Override if your input handler needs validation
     */
    default ValidationResult checkInputValidity(Update update){
        return ValidationResult.builder()
                .isMessageValid(true)
                .message(Strings.EMPTY)
                .build();
    }

    default Long getChatId(@NotNull Update update) {
        return update.getMessage().getChatId();
    }
    default String getText(@NotNull Update update) {
        return update.getMessage().getText();
    }
}
