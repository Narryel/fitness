package com.narryel.fitness.bot.handlers.input;

import com.narryel.fitness.domain.entity.FitUser;
import com.narryel.fitness.domain.entity.Training;
import com.narryel.fitness.domain.entity.UserState;
import com.narryel.fitness.domain.enums.State;
import com.narryel.fitness.domain.enums.TrainingStatus;
import com.narryel.fitness.exceptions.EntityNotFoundException;
import com.narryel.fitness.repository.FitUserRepository;
import com.narryel.fitness.repository.TrainingRepository;
import com.narryel.fitness.repository.UserStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

import static com.narryel.fitness.domain.enums.State.WAITING_FOR_EXERCISE_NAME;

@Service
@RequiredArgsConstructor
public class TrainingNameInputHandler implements UserInputHandler {

    private final UserStateRepository stateRepository;
    private final FitUserRepository userRepository;
    private final TrainingRepository trainingRepository;

    @Override
    @Transactional
    public SendMessage handle(Update update) {

        final var user = update.getMessage().getFrom();
        final var trainingName = getText(update);
        final var fitUser = userRepository.findByTelegramUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("telegramId", user.getId().toString(), FitUser.class));

        final var training = trainingRepository.save(
                new Training()
                        .setUser(fitUser)
                        .setStatus(TrainingStatus.IN_PLANNING)
                        .setName(trainingName));

        final var message = new SendMessage();
        message.setText("Тренировка \"" + training.getName() + "\" создана. \nВведи имя первого упражнения для добавления");
        message.setChatId(getChatId(update));

        final var state = stateRepository.findByChatId(getChatId(update)).orElseThrow(EntityNotFoundException::new);
        state.setState(WAITING_FOR_EXERCISE_NAME);
        state.setTrainingId(training.getId());
        return message;
    }

    @Override
    public State stateToHandle() {
        return State.WAITING_FOR_TRAINING_NAME;
    }
}
