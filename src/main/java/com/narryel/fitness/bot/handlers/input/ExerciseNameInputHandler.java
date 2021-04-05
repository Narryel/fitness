package com.narryel.fitness.bot.handlers.input;

import com.narryel.fitness.domain.entity.Exercise;
import com.narryel.fitness.domain.entity.UserState;
import com.narryel.fitness.domain.enums.State;
import com.narryel.fitness.domain.enums.TrainingStatus;
import com.narryel.fitness.exceptions.EntityNotFoundException;
import com.narryel.fitness.repository.ExerciseRepository;
import com.narryel.fitness.repository.TrainingRepository;
import com.narryel.fitness.repository.UserStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static com.narryel.fitness.domain.enums.Command.*;
import static com.narryel.fitness.domain.enums.State.WAITING_FOR_EXERCISE_NAME;
import static com.narryel.fitness.util.MessageGenerator.buildButton;
import static com.narryel.fitness.util.MessageGenerator.buildRowWithOneButton;

@Service
@RequiredArgsConstructor
public class ExerciseNameInputHandler implements UserInputHandler {

    private final TrainingRepository trainingRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserStateRepository stateRepository;

    @Override
    @Transactional
    public SendMessage handleUserInput(Update update) {
        final var trainingId = stateRepository.findByChatId(getChatId(update))
                .map(UserState::getTrainingId)
                .orElseThrow(EntityNotFoundException::new);

        stateRepository.deleteByChatId(update.getMessage().getChatId());

        final var trainingName = update.getMessage().getText();

        final var training = trainingRepository.findById(trainingId)
                .orElseThrow(EntityNotFoundException::new);

        exerciseRepository.save(new Exercise()
                .setName(trainingName)
                .setTraining(training)
                .setStatus(TrainingStatus.READY)
        );

        final var exerciseList = exerciseRepository.getAllByTraining(training);

        switch (training.getStatus()) {
            case IN_PLANNING: {
                final var stringBuilder = new StringBuilder("Упражнение добавлено! \n \nТвоя тренировка: \n");
                final var keyboard = new ArrayList<List<InlineKeyboardButton>>();
                exerciseList.forEach(exercise -> stringBuilder.append(exercise.getName()).append("\n"));

                keyboard.add(buildRowWithOneButton("Добавить еще упражнение", ADD_EXERCISE.getValue() + training.getId()));
                keyboard.add(buildRowWithOneButton("Достаточно", setCommandAndIdIntoCallback(FINISH_TRAINING_PLANNING, training.getId())));

                final var sendMessage = new SendMessage();
                sendMessage.setText(stringBuilder.toString());
                sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
                sendMessage.setReplyMarkup(new InlineKeyboardMarkup(keyboard));
                return sendMessage;
            }

            case ACTIVE: {
                final var keyboard = new ArrayList<List<InlineKeyboardButton>>();

                final var stringBuilder = new StringBuilder("Список выполненных упражнений:\n");
                exerciseList.forEach(ex -> {
                    if (ex.getStatus() == TrainingStatus.FINISHED) {
                        stringBuilder.append(ex.getName()).append(String.format(" %s %n", "\u2705")); // <- done emoji
                    } else {
                        keyboard.add(buildRowWithOneButton(ex.getName(), START_EXERCISE.getValue() + " " + ex.getId()));
                    }
                });
                if (keyboard.isEmpty()) {
                    stringBuilder.insert(0, "Супер! Все запланированные упражнения выполнены!\n");
                } else {
                    stringBuilder.insert(0, "Отлично! Какое упражнение делаем следующим?\n");

                }

                keyboard.add(buildRowWithOneButton("Закончить тренировку", FINISH_TRAINING.getValue() + training.getId()));


                final var message = new SendMessage();
                message.setText(stringBuilder.toString());
                message.setReplyMarkup(new InlineKeyboardMarkup(keyboard));
                message.setChatId(String.valueOf(getChatId(update)));
                return message;

            }

            default:
                throw new IllegalStateException("странынй статус у тренировки " + training.getStatus());
        }


    }

    @Override
    public State stateToHandle() {
        return WAITING_FOR_EXERCISE_NAME;
    }

}
