package com.narryel.fitness.util;

import lombok.NoArgsConstructor;
import org.telegram.abilitybots.api.util.Pair;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

import static com.narryel.fitness.util.MessageUtil.generateInlineKeyboard;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class MessageGenerators {
    public static SendMessage getMenu( Long chatId) {
        final var sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(generateInlineKeyboard(List.of(
                Pair.of("Спланировать тренировку", "planTraining"),
                Pair.of("История тренировки", "viewHistory"),
                Pair.of("Начать тренировку", "startTraining"),
                Pair.of("Посмотреть упражнения", "viewExercise")
                ))
        );
//        sendMessage.setText(String.format("%s, Что будем делать?", userName));
        sendMessage.setText("Главное меню");
        sendMessage.setChatId(chatId);
        return sendMessage;
    }
}
