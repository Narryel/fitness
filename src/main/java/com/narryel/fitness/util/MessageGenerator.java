package com.narryel.fitness.util;

import com.narryel.fitness.domain.entity.FitUser;
import com.narryel.fitness.exceptions.EntityNotFoundException;
import com.narryel.fitness.repository.FitUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.util.Pair;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static com.narryel.fitness.domain.enums.Command.*;

@Service
@RequiredArgsConstructor
public class MessageGenerator {

    private final FitUserRepository fitUserRepository;

    public SendMessage getMenu(Long chatId) {

        final var user = fitUserRepository.findByChatId(chatId)
                .orElseThrow(() -> new EntityNotFoundException("chatId", chatId.toString(), FitUser.class));
        final var sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(generateInlineKeyboard(List.of(
                Pair.of("Спланировать тренировку", PLAN_TRAINING.getValue()),
                Pair.of("Начать тренировку", CHOOSE_TRAINING_TO_START.getValue()),
                Pair.of("История тренировок", TRAINING_HISTORY.getValue())
                ))
        );
        sendMessage.setText(String.format("%s, Что будем делать?", user.getNickName()));
        sendMessage.setChatId(chatId);
        return sendMessage;
    }

    public static List<InlineKeyboardButton> createInlineRow(Pair<String, String> nameToCallbackPair) {
        return List.of(
                new InlineKeyboardButton()
                        .setText(nameToCallbackPair.a())
                        .setCallbackData(nameToCallbackPair.b())
        );
    }

    public static List<InlineKeyboardButton> createMenuInlineRow() {
        return List.of(new InlineKeyboardButton().setText("Меню").setCallbackData(GET_MENU.getValue()));
    }


    public static InlineKeyboardMarkup generateInlineKeyboard(List<Pair<String, String>> keyboardInfo) {

        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        keyboardInfo.forEach(pair -> keyboard.add(List.of(new InlineKeyboardButton()
                .setText(pair.a())
                .setCallbackData(pair.b())
        )));

        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup generateOnlyMenuInlineKeyboard() {
        return generateInlineKeyboard(List.of(Pair.of("Меню", GET_MENU.getValue())));
    }
}
