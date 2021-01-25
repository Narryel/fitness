package com.narryel.fitness.bot.handlers.input;

import com.narryel.fitness.domain.entity.Exercise;
import com.narryel.fitness.domain.entity.UserState;
import com.narryel.fitness.domain.enums.State;
import com.narryel.fitness.domain.enums.TrainingStatus;
import com.narryel.fitness.exceptions.EntityNotFoundException;
import com.narryel.fitness.repository.ExerciseRepository;
import com.narryel.fitness.repository.FitUserRepository;
import com.narryel.fitness.repository.TrainingRepository;
import com.narryel.fitness.repository.UserStateRepository;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class ExerciseNameInputHandler implements UserInputHandler {

    private final TrainingRepository trainingRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserStateRepository stateRepository;

    @Override
    @Transactional
    public SendMessage handle(Update update) {
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

        switch (training.getStatus()){
            case IN_PLANNING: {
                final var stringBuilder = new StringBuilder("Упражнение добавлено! \n \nТвоя тренировка: \n");
                final var keyboard = new ArrayList<List<InlineKeyboardButton>>();
                exerciseList.forEach(exercise -> stringBuilder.append(exercise.getName()).append("\n"));

                keyboard.add(List.of(new InlineKeyboardButton().setText("Добавить еще упражнение").setCallbackData(ADD_EXERCISE.getValue() + training.getId())));
                keyboard.add(List.of(new InlineKeyboardButton().setText("Достаточно").setCallbackData(FINISH_TRAINING_PLANNING.getValue())));

                final var sendMessage = new SendMessage();
                sendMessage.setText(stringBuilder.toString());
                sendMessage.setChatId(update.getMessage().getChatId());
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
                        keyboard.add(
                                List.of(new InlineKeyboardButton()
                                        .setText(ex.getName())
                                        .setCallbackData(START_EXERCISE.getValue() + " " + ex.getId())
                                )
                        );
                    }
                });
                if (keyboard.isEmpty()) {
                    stringBuilder.insert(0, "Супер! Все запланированные упражнения выполнены!\n");

                } else {
                    stringBuilder.insert(0, "Отлично! Какое упражнение делаем следующим?\n");

                }

                keyboard.add(List.of(new InlineKeyboardButton().setText("Закончить тренировку").setCallbackData(FINISH_TRAINING.getValue() + training.getId())));


                final var message = new SendMessage();
                message.setText(stringBuilder.toString());
                message.setReplyMarkup(new InlineKeyboardMarkup(keyboard));
                message.setChatId(getChatId(update));
                return message;

            }

            default: throw new IllegalStateException("странынй статус у тренировки "+ training.getStatus());
        }




    }

    @Override
    public State stateToHandle() {
        return WAITING_FOR_EXERCISE_NAME;
    }


}
