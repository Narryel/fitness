package com.narryel.fitness.bot.handlers.command;

import com.narryel.fitness.domain.entity.Training;
import com.narryel.fitness.domain.enums.Command;
import com.narryel.fitness.domain.enums.TrainingStatus;
import com.narryel.fitness.exceptions.EntityNotFoundException;
import com.narryel.fitness.repository.TrainingRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.abilitybots.api.util.Pair;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static com.narryel.fitness.domain.enums.Command.*;
import static com.narryel.fitness.util.MessageGenerator.createInlineRow;

@Service
@RequiredArgsConstructor
public class StartTrainingCommandHandler implements CommandHandler {

    private final TrainingRepository trainingRepository;

    @Override
    @Transactional
    public SendMessage handleCommand(Update update) {
        final var chatId = getChatId(update);

        val trainingId = getEntityIdFromUpdate(update);
        val training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new EntityNotFoundException(trainingId, Training.class));

        training.setStatus(TrainingStatus.ACTIVE);

        val keyboard = new ArrayList<List<InlineKeyboardButton>>();
        training.getExercises().forEach(exercise -> keyboard.add(
                List.of(new InlineKeyboardButton()
                        .setText(exercise.getName())
                        .setCallbackData(String.format("%s %d", START_EXERCISE.getValue(), exercise.getId())))
        ));

        keyboard.add(createInlineRow(Pair.of("Добавить еще упражнение", ADD_EXERCISE.getValue() + training.getId())));
        keyboard.add(createInlineRow(Pair.of("Закончить тренировку", FINISH_TRAINING.getValue() + training.getId())));

        val message = new SendMessage();
        message.setText("К какому упражнению приступим?");
        message.setReplyMarkup(new InlineKeyboardMarkup(keyboard));
        message.setChatId(chatId);
        return message;
    }

    @NotNull
    @Override
    public Command commandToHandle() {
        return START_TRAINING;
    }
}
