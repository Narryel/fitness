package com.narryel.fitness.util;

import com.narryel.fitness.domain.entity.FitUser;
import com.narryel.fitness.exceptions.EntityNotFoundException;
import com.narryel.fitness.repository.FitUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.util.Pair;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

import static com.narryel.fitness.domain.enums.Command.CHOOSE_TRAINING_TO_START;
import static com.narryel.fitness.domain.enums.Command.PLAN_TRAINING;
import static com.narryel.fitness.util.MessageUtil.generateInlineKeyboard;

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
                Pair.of("Начать тренировку", CHOOSE_TRAINING_TO_START.getValue())
//                Pair.of("История тренировки", "viewHistory"),
//                Pair.of("Посмотреть упражнения", "viewExercise")
                ))
        );
        sendMessage.setText(String.format("%s, Что будем делать?", user.getNickName()));
        sendMessage.setChatId(chatId);
        return sendMessage;
    }
}
