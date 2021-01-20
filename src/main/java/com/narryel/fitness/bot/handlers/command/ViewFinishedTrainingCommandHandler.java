package com.narryel.fitness.bot.handlers.command;

import com.narryel.fitness.domain.entity.Training;
import com.narryel.fitness.domain.enums.Command;
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

import java.util.List;

import static com.narryel.fitness.domain.enums.Command.*;
import static com.narryel.fitness.domain.enums.TrainingStatus.FINISHED;
import static com.narryel.fitness.util.MessageGenerator.generateInlineKeyboard;


@Service
@RequiredArgsConstructor
public class ViewFinishedTrainingCommandHandler implements CommandHandler {

    private final TrainingRepository trainingRepository;

    @Override
    @Transactional
    public SendMessage handleCommand(Update update) {
        val trainingId = Long.valueOf(getData(update).replace(VIEW_FINISHED_TRAINING.getValue(), ""));
        val training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new EntityNotFoundException(trainingId, Training.class));

        val trainingReport = getTrainingReport(training);


        val keyboard = generateInlineKeyboard(
                List.of(
                        Pair.of("Удалить тренировку", DELETE_TRAINING_FROM_HISTORY.getValue() + training.getId()),
                        Pair.of("Повторить тренировку", REDO_TRAINING.getValue() + training.getId()),
                        Pair.of("Меню", GET_MENU.getValue())
                )
        );

        val message = new SendMessage();
        message.setText(trainingReport.toString());
        message.setChatId(getChatId(update));
        message.setReplyMarkup(keyboard);
        return message;
    }

    @NotNull
    @Override
    public Command commandToHandle() {
        return VIEW_FINISHED_TRAINING;
    }

    @NotNull
    private StringBuilder getTrainingReport(Training training) {
        final var stringBuilder = new StringBuilder("Отчет по тренировке \"" + training.getName() + "\": \n");
        training.getExercises()
                .stream()
                .filter(exercise -> FINISHED.equals(exercise.getStatus()))
                .forEachOrdered(exercise -> {
                    stringBuilder.append(exercise.getName()).append(": \n");
                    exercise.getSets().forEach(set -> stringBuilder.append(String.format("  Подход %d: %dx%s. %n", set.getSetOrder(), set.getRepCount(), set.getWeight().toPlainString())));
                });
        return stringBuilder;
    }
}
