package com.narryel.fitness.bot.handlers.input;

import com.narryel.fitness.bot.handlers.input.validation.ValidationResult;
import com.narryel.fitness.domain.enums.Command;
import com.narryel.fitness.domain.enums.State;
import lombok.val;
import org.apache.logging.log4j.util.Strings;
import org.glassfish.jersey.internal.inject.InjectionResolverBinding;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public interface UserInputHandler {
    SendMessage handleUserInput(Update update);

    State stateToHandle();

    /**
     * Override if your input handler needs validation
     */
    default ValidationResult checkInputValidity(Update update) {
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

    default String setCommandAndIdIntoCallback(Command command, Long id) {
        return command.getValue() + id.toString();
    }
}
