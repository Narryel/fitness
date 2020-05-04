package com.narryel.fitness.bot.handlers.command;

import com.narryel.fitness.domain.entity.Training;
import com.narryel.fitness.domain.enums.Command;
import com.narryel.fitness.domain.enums.TrainingStatus;
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
import java.util.Objects;

import static com.narryel.fitness.domain.enums.Command.*;

@Service
@RequiredArgsConstructor
public class StartTrainingCommandHandler implements CommandHandler {

    private final TrainingRepository trainingRepository;

    @Override
    @Transactional
    public SendMessage handleCommand(Update update) {
        final var chatId = Objects.requireNonNull(update.getCallbackQuery().getMessage().getChatId());
        final var message = new SendMessage();

        final var data = update.getCallbackQuery().getData();
        //delete prefix from callbackData to get trainingId
        final var trainingId = Long.valueOf(data.replace(START_TRAINING.getValue() + " ", ""));
        final var training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new EntityNotFoundException(trainingId, Training.class));

        training.setStatus(TrainingStatus.ACTIVE);

        final var keyboard = new ArrayList<List<InlineKeyboardButton>>();
        training.getExercises().forEach(exercise -> keyboard.add(
                List.of(new InlineKeyboardButton()
                        .setText(exercise.getName())
                        .setCallbackData(String.format("%s %d", START_EXERCISE.getValue(), exercise.getId())))
        ));
        keyboard.add(List.of(new InlineKeyboardButton().setText("Меню").setCallbackData(GET_MENU.getValue())));

        message.setText("К какому упражнению приступим?");
        message.setReplyMarkup(new InlineKeyboardMarkup(keyboard));
        message.setChatId(chatId);
        return message;
    }

    @Override
    public Command commandToHandle() {
        return START_TRAINING;
    }
}
