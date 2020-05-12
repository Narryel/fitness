package com.narryel.fitness.bot.handlers.input;

import com.narryel.fitness.domain.entity.Exercise;
import com.narryel.fitness.domain.entity.FitUser;
import com.narryel.fitness.domain.entity.Training;
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

    private final FitUserRepository userRepository;
    private final TrainingRepository trainingRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserStateRepository stateRepository;

    @Override
    @Transactional
    public SendMessage handle(Update update) {
        stateRepository.deleteByChatId(update.getMessage().getChatId());

        final var user = update.getMessage().getFrom();
        final var trainingName = update.getMessage().getText();
        final var fitUser = userRepository.findByTelegramUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("telegramId", user.getId().toString(), FitUser.class));

        final var training = trainingRepository.findByUserAndStatusEqualsReady(fitUser)
                .orElseThrow(EntityNotFoundException::new);

        exerciseRepository.save(new Exercise()
                .setName(trainingName)
                .setTraining(training)
                .setStatus(TrainingStatus.READY)
        );

        final var exerciseList = exerciseRepository.getAllByTraining(training);


        final var stringBuilder = new StringBuilder("Упражнение добавлено! \n \nТвоя тренировка: \n");
        final var keyboard = new ArrayList<List<InlineKeyboardButton>>();
        exerciseList.forEach(exercise -> stringBuilder.append(exercise.getName()).append("\n"));

        keyboard.add(List.of(new InlineKeyboardButton().setText("Добавить еще упражнение").setCallbackData(ADD_EXERCISE.getValue())));
        keyboard.add(List.of(new InlineKeyboardButton().setText("Достаточно").setCallbackData(FINISH_TRAINING_PLANNING.getValue())));

        final var sendMessage = new SendMessage();
        sendMessage.setText(stringBuilder.toString());
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setReplyMarkup(new InlineKeyboardMarkup(keyboard));
        return sendMessage;


    }

    @Override
    public State stateToHandle() {
        return WAITING_FOR_EXERCISE_NAME;
    }


}
