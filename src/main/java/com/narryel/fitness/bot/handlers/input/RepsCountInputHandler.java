package com.narryel.fitness.bot.handlers.input;

import com.narryel.fitness.bot.handlers.input.validation.ValidationResult;
import com.narryel.fitness.bot.handlers.input.validation.ValidationMethods;
import com.narryel.fitness.domain.entity.ExerciseSet;
import com.narryel.fitness.domain.enums.State;
import com.narryel.fitness.exceptions.EntityNotFoundException;
import com.narryel.fitness.repository.ExerciseRepository;
import com.narryel.fitness.repository.ExerciseSetRepository;
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

import static com.narryel.fitness.domain.enums.Command.CHANGE_WEIGHT;
import static com.narryel.fitness.domain.enums.Command.FINISH_EXERCISE;
import static com.narryel.fitness.domain.enums.State.WAITING_FOR_REPS;

@Service
@RequiredArgsConstructor
public class RepsCountInputHandler implements UserInputHandler {

    private static final String INVALID_INPUT_MESSAGE = "Введите число повторений (целое число, больше нуля)";
    private final ExerciseSetRepository setRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserStateRepository stateRepository;
    private final ValidationMethods validationMethods;

    @Override
    @Transactional
    public SendMessage handle(Update update) {
        final var chatId = this.getChatId(update);
        final var repsCount = Integer.valueOf(this.getText(update));
        final var exerciseId = stateRepository.findByChatId(chatId).orElseThrow(EntityNotFoundException::new).getExerciseId();

        final var exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(EntityNotFoundException::new);

        setRepository.save(new ExerciseSet()
                .setExercise(exercise)
                .setRepCount(repsCount)
                .setWeight(exercise.getWeight())
                .setSetOrder(exercise.getSets() == null ? 1 : exercise.getSets().size() + 1)
        );

        final var setList = setRepository.getAllByExercise(exercise);
        final var stringBuilder = new StringBuilder("Текущее упражнение: ").append(exercise.getName()).append("\n");
        setList.forEach(set -> stringBuilder.append(String.format("Подход %d. %dx%s. %n", set.getSetOrder(), set.getRepCount(), set.getWeight())));

        stringBuilder.append("\n\nВведите кол-во повторений или выберите опцию:");
        final var keyboard = new ArrayList<List<InlineKeyboardButton>>();
        keyboard.add(List.of(new InlineKeyboardButton().setText("Изменить вес").setCallbackData(CHANGE_WEIGHT.getValue() + "" + exercise.getId())));
        keyboard.add(List.of(new InlineKeyboardButton().setText("Закончить упражнение").setCallbackData(FINISH_EXERCISE.getValue() + " " + exercise.getId())));

        final var sendMessage = new SendMessage();
        sendMessage.setText(stringBuilder.toString());
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setReplyMarkup(new InlineKeyboardMarkup(keyboard));
        return sendMessage;

    }

    @Override
    public State stateToHandle() {
        return WAITING_FOR_REPS;
    }

    @Override
    public ValidationResult checkInputValidity(Update update) {
        //todo change validation to integer or migrate db to bigDecimal
        return validationMethods.checkIfPositiveInteger(update, INVALID_INPUT_MESSAGE);
    }
}
