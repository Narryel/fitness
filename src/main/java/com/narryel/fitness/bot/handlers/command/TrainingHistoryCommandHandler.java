package com.narryel.fitness.bot.handlers.command;

import com.narryel.fitness.domain.entity.FitUser;
import com.narryel.fitness.domain.entity.Training;
import com.narryel.fitness.domain.enums.Command;
import com.narryel.fitness.domain.enums.TrainingStatus;
import com.narryel.fitness.exceptions.EntityNotFoundException;
import com.narryel.fitness.repository.FitUserRepository;
import com.narryel.fitness.repository.TrainingRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.narryel.fitness.domain.enums.Command.*;
import static com.narryel.fitness.util.MessageGenerator.createMenuInlineRow;

@Service
@RequiredArgsConstructor
public class TrainingHistoryCommandHandler implements CommandHandler {

    private final FitUserRepository userRepository;
    private final TrainingRepository trainingRepository;


    @Override
    public SendMessage handleCommand(Update update) {
        val chatId = getChatId(update);
        val user = userRepository.findByChatId(chatId)
                .orElseThrow(() -> new EntityNotFoundException("chatId", chatId, FitUser.class));

        val trainingList = trainingRepository.findByUserAndStatus(user, TrainingStatus.FINISHED);
        val keyboard = getTrainingHistoryKeyboard(trainingList);
        val message = trainingList.isEmpty()? "История тренировок пуста.": "История тренировок. \n Выбери тренировку:";

        val sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(keyboard);
        return sendMessage;
    }

    @NotNull
    public InlineKeyboardMarkup getTrainingHistoryKeyboard(List<Training> trainingList) {

        val keyboard = new ArrayList<List<InlineKeyboardButton>>();
        trainingList.forEach(training -> keyboard.add(
                List.of(new InlineKeyboardButton()
                        .setText(String.format("Тренировка \"%s\" от %s", training.getName(), getDateFromInstant(training.getPrePersistDate())))
                        .setCallbackData(VIEW_FINISHED_TRAINING.getValue() + training.getId()))
        ));
        keyboard.add(createMenuInlineRow());
        return new InlineKeyboardMarkup(keyboard);
    }

    private String getDateFromInstant(Instant instant) {
        LocalDateTime datetime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return DateTimeFormatter.ofPattern("dd.MM").format(datetime);
    }


    @NotNull
    @Override
    public Command commandToHandle() {
        return TRAINING_HISTORY;
    }
}
