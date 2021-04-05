package com.narryel.fitness.util;

import com.narryel.fitness.domain.enums.Command;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.Predicate;

public class UpdateCheckUtils {

    private UpdateCheckUtils(){
    }

    public static Predicate<Update> callbackDataEquals(Command command) {
        return update -> {
            CallbackQuery callbackData = update.getCallbackQuery();
            if (callbackData == null) {
                return false;
            }
            return command.getValue().equals(callbackData.getData());
        };
    }

    public static Predicate<Update> callbackDataContains(Command command) {
        return update -> {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            if (callbackQuery == null) {
                return false;
            }
            return callbackQuery.getData().contains(command.getValue());
        };
    }

    public static Predicate<Update> textEquals(Command command) {
        return update -> {
            val message = update.getMessage();
            if (message == null) {
                return false;
            }
            if (!message.hasText()) {
                return false;
            }
            return command.getValue().equals(message.getText());
        };
    }

}
