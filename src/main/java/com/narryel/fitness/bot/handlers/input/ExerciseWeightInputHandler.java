package com.narryel.fitness.bot.handlers.input;

import com.narryel.fitness.bot.handlers.input.validation.ValidationResult;
import com.narryel.fitness.bot.handlers.input.validation.ValidationMethods;
import com.narryel.fitness.domain.entity.Exercise;
import com.narryel.fitness.domain.entity.UserState;
import com.narryel.fitness.domain.enums.State;
import com.narryel.fitness.exceptions.EntityNotFoundException;
import com.narryel.fitness.repository.ExerciseRepository;
import com.narryel.fitness.repository.UserStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;

import static com.narryel.fitness.domain.enums.State.WAITING_FOR_REPS;

@Service
@RequiredArgsConstructor
public class ExerciseWeightInputHandler implements UserInputHandler {

    private final ExerciseRepository exerciseRepository;
    private final UserStateRepository stateRepository;
    private final ValidationMethods validationMethods;
    private static final String INVALID_INPUT_MESSAGE = "Введите вес, с которым будете делать упражнение (число больше нуля)";



    @Override
    @Transactional
    public SendMessage handle(Update update) {
        final var chatId = update.getMessage().getChatId();
        final var state = stateRepository.findByChatId(chatId)
                .orElseThrow(() -> new EntityNotFoundException("chatId", chatId.toString(), UserState.class));

        final var exercise = exerciseRepository.findById(state.getExerciseId())
                .orElseThrow(() -> new EntityNotFoundException(state.getChatId(), Exercise.class));

        final var text = update.getMessage().getText();
        final var replaced = text.replace(",", ".");

        final var weight = new BigDecimal(replaced);
        exercise.setWeight(weight);

        state.setState(WAITING_FOR_REPS).setExerciseId(exercise.getId());

        final var message = new SendMessage();
        message.setText(String.format("Вес задан на %s . %nНачинаем подход! %nВведите количество повторений", weight.toPlainString()));
        message.setChatId(chatId);
        return message;
    }

    @Override
    public ValidationResult checkInputValidity(Update update) {
        return validationMethods.checkIfPositiveBigDecimal(update, INVALID_INPUT_MESSAGE);
    }

    @Override
    public State stateToHandle() {
        return State.WAITING_FOR_WEIGHT;
    }
}
