package com.narryel.fitness.bot.handlers.command;

import com.narryel.fitness.bot.handlers.UpdateHandler;
import com.narryel.fitness.domain.enums.Command;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.Predicate;

import static com.narryel.fitness.util.UpdateCheckUtils.callbackDataContains;

public abstract class CommandHandler implements UpdateHandler {


    @NotNull
    protected abstract Command commandToHandle();

    @NotNull
    protected final Long getEntityIdFromUpdate(@NotNull Update update) {
        return Long.valueOf(getData(update).replace(commandToHandle().getValue(), ""));
    }

    @Override
    public final Reply getRespondingReply() {
        return Reply.of(
                (bot, update) -> {
                    val sendMessage = handleCommand(update);
                    bot.silent().execute(sendMessage);
                },
                getHandlerPredicate()
        );
    }

    protected Predicate<Update> getHandlerPredicate() {
        return callbackDataContains(commandToHandle());
    }

    public abstract SendMessage handleCommand(Update update);

    protected final String getChatId(@NotNull Update update) {
        return String.valueOf(update.getCallbackQuery().getMessage().getChatId());
    }

    protected final String getData(@NotNull Update update) {
        return update.getCallbackQuery().getData();
    }

}
