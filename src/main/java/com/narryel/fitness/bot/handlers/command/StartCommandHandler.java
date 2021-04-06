package com.narryel.fitness.bot.handlers.command;


import com.narryel.fitness.domain.enums.Command;
import com.narryel.fitness.repository.FitUserRepository;
import com.narryel.fitness.util.MessageGenerator;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.function.Predicate;

import static com.narryel.fitness.domain.enums.Command.REGISTER_USER;
import static com.narryel.fitness.domain.enums.Command.START;
import static com.narryel.fitness.util.UpdateCheckUtils.textEquals;

@Service
@RequiredArgsConstructor
public class StartCommandHandler extends CommandHandler {
    private final FitUserRepository userRepository;
    private final MessageGenerator messageGenerator;


    @Override
    public SendMessage handleCommand(Update update) {
        val chatId = update.getMessage().getChatId();
        val optionalFitUser = userRepository.findByChatId(chatId);

        if (optionalFitUser.isPresent()) {
            return messageGenerator.getMenu(chatId);

        } else {

            val message = new SendMessage();
            val inlineKeyboardMarkup = new InlineKeyboardMarkup();
            val button1 = new InlineKeyboardButton();
            button1.setText("Зарегистрироваться");
            button1.setCallbackData(REGISTER_USER.getValue());
            inlineKeyboardMarkup.setKeyboard(List.of(List.of(button1)));

            message.setChatId(String.valueOf(chatId));
            message.setText("Новенький у нас? зарегистрируемся?");
            message.setReplyMarkup(inlineKeyboardMarkup);
            return message;
        }


    }

    @Override
    protected Predicate<Update> getHandlerPredicate() {
        return textEquals(START);
    }

    @NotNull
    @Override
    public Command commandToHandle() {
        return START;
    }

}
