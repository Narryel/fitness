package com.narryel.fitness.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.telegram.abilitybots.api.util.Pair;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageUtil {


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

}
