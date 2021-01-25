package com.narryel.fitness.bot.handlers.command;

import com.narryel.fitness.domain.entity.Training;
import com.narryel.fitness.domain.enums.Command;
import com.narryel.fitness.exceptions.EntityNotFoundException;
import com.narryel.fitness.repository.ExerciseRepository;
import com.narryel.fitness.repository.TrainingRepository;
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

import static com.narryel.fitness.domain.enums.Command.FINISH_TRAINING;
import static com.narryel.fitness.domain.enums.Command.GET_MENU;
import static com.narryel.fitness.domain.enums.TrainingStatus.FINISHED;

@Service
@RequiredArgsConstructor
public class FinishTrainingCommandHandler implements CommandHandler {

    private final TrainingRepository trainingRepository;
    private final ExerciseRepository exerciseRepository;

    @Override
    @Transactional
    public SendMessage handleCommand(Update update) {
        val trainingId = Long.valueOf(getData(update).replace(FINISH_TRAINING.getValue(), ""));
        val training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new EntityNotFoundException(trainingId, Training.class));
        training.setStatus(FINISHED);

        val stringBuilder = new StringBuilder("Отчет по тренировке: \n");
        exerciseRepository.getAllByTraining(training)
                .stream()
                .filter(exercise -> FINISHED.equals(exercise.getStatus()))
                .forEachOrdered(exercise -> {
                    stringBuilder.append(exercise.getName()).append(": \n");
                    exercise.getSets().forEach(set -> stringBuilder.append(String.format("  Подход %d: %dx%s. %n", set.getSetOrder(), set.getRepCount(), set.getWeight().toPlainString())));
                });


        val keyboard = new ArrayList<List<InlineKeyboardButton>>();
        keyboard.add(List.of(new InlineKeyboardButton().setText("Меню").setCallbackData(GET_MENU.getValue())));

        val message = new SendMessage();
        message.setText(stringBuilder.toString());
        message.setChatId(getChatId(update));
        message.setReplyMarkup(new InlineKeyboardMarkup(keyboard));
        return message;
    }

    @NotNull
    @Override
    public Command commandToHandle() {
        return FINISH_TRAINING;
    }
}
