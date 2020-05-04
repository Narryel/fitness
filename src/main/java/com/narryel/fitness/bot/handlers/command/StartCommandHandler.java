package com.narryel.fitness.bot.handlers.command;


import com.narryel.fitness.domain.entity.FitUser;
import com.narryel.fitness.domain.enums.Command;
import com.narryel.fitness.repository.FitUserRepository;
import com.narryel.fitness.util.MessageGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static com.narryel.fitness.domain.enums.Command.REGISTER_USER;
import static com.narryel.fitness.domain.enums.Command.START;

@Service
@RequiredArgsConstructor
public class StartCommandHandler implements CommandHandler {
    private final FitUserRepository userRepository;
    private final MessageGenerator messageGenerator;


    @Override
    public SendMessage handleCommand(Update update) {
        //do not use CommandHandler.getChatId() cause its not a callback, but a message
        final var chatId = getChatId(update);
        final var optionalFitUser = userRepository.findByChatId(chatId);

        if (optionalFitUser.isPresent()) {
            return messageGenerator.getMenu(chatId);

        } else {

            final var message = new SendMessage();
            final var inlineKeyboardMarkup = new InlineKeyboardMarkup();
            final var button1 = new InlineKeyboardButton();
            button1.setText("Зарегистрироваться");
            button1.setCallbackData(REGISTER_USER.getValue());
            inlineKeyboardMarkup.setKeyboard(List.of(List.of(button1)));

            message.setChatId(chatId);
            message.setText("Новенький у нас? зарегистрируемся?");
            message.setReplyMarkup(inlineKeyboardMarkup);
            return message;
        }


    }

    @Override
    public Command commandToHandle() {
        return START;
    }

    @Override
    public Long getChatId(Update update) {
        return update.getMessage().getChatId();
    }
}
