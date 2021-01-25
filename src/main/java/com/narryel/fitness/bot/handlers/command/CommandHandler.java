package com.narryel.fitness.bot.handlers.command;

import com.narryel.fitness.domain.enums.Command;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandHandler {

    SendMessage handleCommand(Update update);

    @NotNull Command commandToHandle();

    default Long getChatId(@NotNull Update update) {
        return update.getCallbackQuery().getMessage().getChatId();
    }

    default String getData(@NotNull Update update) {
        return update.getCallbackQuery().getData();
    }

    default Long getEntityIdFromUpdate(@NotNull Update update){
        return Long.valueOf(getData(update).replace(commandToHandle().getValue(), ""));
    }
}
