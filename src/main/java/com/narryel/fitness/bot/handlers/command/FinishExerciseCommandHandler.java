package com.narryel.fitness.bot.handlers.command;

import com.narryel.fitness.domain.entity.Exercise;
import com.narryel.fitness.domain.enums.Command;
import com.narryel.fitness.domain.enums.TrainingStatus;
import com.narryel.fitness.exceptions.EntityNotFoundException;
import com.narryel.fitness.repository.ExerciseRepository;
import com.narryel.fitness.repository.UserStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static com.narryel.fitness.domain.enums.Command.*;

@Service
@RequiredArgsConstructor
public class FinishExerciseCommandHandler extends CommandHandler {

    private final UserStateRepository stateRepository;
    private final ExerciseRepository exerciseRepository;

    @Override
    @Transactional
    public SendMessage handleCommand(Update update) {
        final var chatId = getChatId(update);
        final var exerciseId = Long.valueOf(getData(update).replace(FINISH_EXERCISE.getValue() + " ", ""));
        stateRepository.deleteByChatId(Long.valueOf(chatId));

        final var exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new EntityNotFoundException(exerciseId, Exercise.class));
        exercise.setStatus(TrainingStatus.FINISHED);

        final var exerciseList = exercise.getTraining().getExercises();
        final var keyboard = new ArrayList<List<InlineKeyboardButton>>();

        final var stringBuilder = new StringBuilder("Список выполненных упражнений:\n");
        exerciseList.forEach(ex -> {
            if (ex.getStatus() == TrainingStatus.FINISHED) {
                stringBuilder.append(ex.getName()).append(String.format(" %s %n", "\u2705"));
            } else {
                val exerciseButton = new InlineKeyboardButton();
                exerciseButton.setText(ex.getName());
                exerciseButton.setCallbackData(START_EXERCISE.getValue() + " " + ex.getId());
                keyboard.add(List.of(exerciseButton));
            }
        });
        if (keyboard.isEmpty()) {
            stringBuilder.insert(0, "Супер! Все запланированные упражнения выполнены!\n");

        } else {
            stringBuilder.insert(0, "Отлично! Какое упражнение делаем следующим?\n");

        }
        val addExerciseButton = new InlineKeyboardButton();
        addExerciseButton.setText("Добавить еще упражнение");
        addExerciseButton.setCallbackData(ADD_EXERCISE.getValue() + exercise.getTraining().getId());
        keyboard.add(List.of(addExerciseButton));
        val finishTrainingButton = new InlineKeyboardButton();

        finishTrainingButton.setText("Закончить тренировку");
        finishTrainingButton.setCallbackData(FINISH_TRAINING.getValue() + exercise.getTraining().getId());
        keyboard.add(List.of(finishTrainingButton));


        final var message = new SendMessage();
        message.setText(stringBuilder.toString());
        message.setReplyMarkup(new InlineKeyboardMarkup(keyboard));
        message.setChatId(chatId);
        return message;
    }

    @NotNull
    @Override
    public Command commandToHandle() {
        return Command.FINISH_EXERCISE;
    }

}
