package com.narryel.fitness.bot.handlers.command;

import com.narryel.fitness.domain.enums.Command;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.narryel.fitness.util.MessageGenerator.generateOnlyMenuInlineKeyboard;

@Service
public class RedoTrainingCommandHandler implements CommandHandler {

    @Override
    public SendMessage handleCommand(Update update) {
        val sendMessage = new SendMessage(getChatId(update), "Автор еще пока не доделал данную фичу. \n Можете попинать его, вдруг сделает быстрее");
        sendMessage.setReplyMarkup(generateOnlyMenuInlineKeyboard());
        return sendMessage;
    }

    @NotNull
    @Override
    public Command commandToHandle() {
        return Command.REDO_TRAINING;
    }
}
