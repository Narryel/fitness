package com.narryel.fitness.bot.handlers.command;

import com.narryel.fitness.domain.entity.Training;
import com.narryel.fitness.domain.enums.Command;
import com.narryel.fitness.exceptions.EntityNotFoundException;
import com.narryel.fitness.repository.TrainingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static com.narryel.fitness.domain.enums.Command.GET_MENU;
import static com.narryel.fitness.domain.enums.Command.VIEW_FINISHED_TRAINING;
import static com.narryel.fitness.domain.enums.TrainingStatus.FINISHED;


@Service
@RequiredArgsConstructor
public class ViewFinishedTrainingCommandHandler implements CommandHandler {

    private final TrainingRepository trainingRepository;

    @Override
    @Transactional
    public SendMessage handleCommand(Update update) {
        final var trainingId = Long.valueOf(getData(update).replace(VIEW_FINISHED_TRAINING.getValue(), ""));
        final var training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new EntityNotFoundException(trainingId, Training.class));

        //todo extract in method?
        final var stringBuilder = new StringBuilder("Отчет по тренировке \""+ training.getName() +"\": \n");
        training.getExercises()
                .stream()
                .filter(exercise -> FINISHED.equals(exercise.getStatus()))
                .forEachOrdered(exercise -> {
                    stringBuilder.append(exercise.getName()).append(": \n");
                    exercise.getSets().forEach(set -> stringBuilder.append(String.format("  Подход %d: %dx%s. %n", set.getSetOrder(), set.getRepCount(), set.getWeight().toPlainString())));
                });


        final var keyboard = new ArrayList<List<InlineKeyboardButton>>();
        keyboard.add(List.of(new InlineKeyboardButton().setText("Меню").setCallbackData(GET_MENU.getValue())));

        final var message = new SendMessage();
        message.setText(stringBuilder.toString());
        message.setChatId(getChatId(update));
        message.setReplyMarkup(new InlineKeyboardMarkup(keyboard));
        return message;
    }

    @Override
    public Command commandToHandle() {
        return VIEW_FINISHED_TRAINING;
    }
}
