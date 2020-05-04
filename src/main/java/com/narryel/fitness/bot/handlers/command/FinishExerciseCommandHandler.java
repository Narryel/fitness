package com.narryel.fitness.bot.handlers.command;

import com.narryel.fitness.domain.entity.Exercise;
import com.narryel.fitness.domain.entity.FitUser;
import com.narryel.fitness.domain.enums.Command;
import com.narryel.fitness.domain.enums.TrainingStatus;
import com.narryel.fitness.exceptions.EntityNotFoundException;
import com.narryel.fitness.repository.ExerciseRepository;
import com.narryel.fitness.repository.FitUserRepository;
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

@Service
@RequiredArgsConstructor
public class FinishExerciseCommandHandler implements CommandHandler {

    private final UserStateRepository stateRepository;
    private final ExerciseRepository exerciseRepository;
    private final FitUserRepository userRepository;

    @Override
    @Transactional
    public SendMessage handleCommand(Update update) {
        final var chatId = getChatId(update);
        final var exerciseId = Long.valueOf(getData(update).replace(FINISH_EXERCISE.getValue() + " ", ""));
        stateRepository.deleteByChatId(chatId);

        final var exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new EntityNotFoundException(exerciseId, Exercise.class));
        exercise.setStatus(TrainingStatus.FINISHED);

        final var exerciseList = exercise.getTraining().getExercises();
        final var keyboard = new ArrayList<List<InlineKeyboardButton>>();

        final var stringBuilder = new StringBuilder("Список выполненных упражнений:\n");
        exerciseList.forEach(ex -> {
            switch (ex.getStatus()) {
                case FINISHED: {
                    stringBuilder.append(ex.getName()).append(String.format(" %s %n", "\u2705"));
                    break;
                }
                default: {
                    keyboard.add(
                            List.of(new InlineKeyboardButton()
                                    .setText(ex.getName())
                                    .setCallbackData(START_EXERCISE.getValue() + " " + ex.getId())
                            )
                    );
                    break;
                }
            }
        });
        if (keyboard.isEmpty()) {
            stringBuilder.insert(0, "Супер! Все запланированные упражнения выполнены!\n");

        } else {
            stringBuilder.insert(0, "Отлично! Какое упражнение делаем следующим?\n");

        }

        keyboard.add(List.of(new InlineKeyboardButton().setText("Закончить тренировку").setCallbackData(FINISH_TRAINING.getValue() + exercise.getTraining().getId())));


        final var message = new SendMessage();
        message.setText(stringBuilder.toString());
        message.setReplyMarkup(new InlineKeyboardMarkup(keyboard));
        message.setChatId(chatId);
        return message;
    }

    @Override
    public Command commandToHandle() {
        return Command.FINISH_EXERCISE;
    }
}
