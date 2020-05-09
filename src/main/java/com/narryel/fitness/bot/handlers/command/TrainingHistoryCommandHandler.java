package com.narryel.fitness.bot.handlers.command;

import com.narryel.fitness.domain.entity.FitUser;
import com.narryel.fitness.domain.enums.Command;
import com.narryel.fitness.domain.enums.TrainingStatus;
import com.narryel.fitness.exceptions.EntityNotFoundException;
import com.narryel.fitness.repository.FitUserRepository;
import com.narryel.fitness.repository.TrainingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.narryel.fitness.domain.enums.Command.*;

@Service
@RequiredArgsConstructor
public class TrainingHistoryCommandHandler implements CommandHandler {

    private final FitUserRepository userRepository;
    private final TrainingRepository trainingRepository;


    @Override
    public SendMessage handleCommand(Update update) {
        final var chatId = getChatId(update);
        final var user = userRepository.findByChatId(chatId)
                .orElseThrow(() -> new EntityNotFoundException("chatId", chatId, FitUser.class));

        final var trainingList = trainingRepository.findByUserAndStatus(user, TrainingStatus.FINISHED);

        final var keyboard = new ArrayList<List<InlineKeyboardButton>>();
        trainingList.forEach(training -> keyboard.add(
                List.of(new InlineKeyboardButton()
                        .setText(String.format("Тренировка \"%s\" от %s", training.getName(), getDateFromInstant(training.getPrePersistDate())))
                        .setCallbackData(VIEW_FINISHED_TRAINING.getValue() + training.getId()))
        ));
        keyboard.add(List.of(new InlineKeyboardButton().setText("Меню").setCallbackData(GET_MENU.getValue())));


        final var sendMessage = new SendMessage();
        sendMessage.setText("Выбери тренировку");
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(new InlineKeyboardMarkup(keyboard));
        return sendMessage;
    }

    private String getDateFromInstant(Instant instant) {
        LocalDateTime datetime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return DateTimeFormatter.ofPattern("dd.MM").format(datetime);
    }


    @Override
    public Command commandToHandle() {
        return TRAINING_HISTORY;
    }
}
