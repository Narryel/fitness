package com.narryel.fitness.bot.handlers.command;

import com.narryel.fitness.domain.entity.FitUser;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.narryel.fitness.domain.enums.Command.*;

@Service
@RequiredArgsConstructor
public class ChooseTrainingToStartCommandHandler implements CommandHandler {

    private final FitUserRepository userRepository;
    private final TrainingRepository trainingRepository;

    @Override
    public SendMessage handleCommand(Update update) {
        val chatId = Objects.requireNonNull(update.getCallbackQuery().getMessage().getChatId());

        val fitUser = userRepository.findByChatId(chatId)
                .orElseThrow(() -> new EntityNotFoundException("chatId", chatId.toString(), FitUser.class));

        val plannedTrainingList = trainingRepository.findByUserAndStatus(fitUser, TrainingStatus.READY);
        if (plannedTrainingList.isEmpty()) {
            val message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Не найдено запланированных тренировок.");
            val keyboard = new ArrayList<List<InlineKeyboardButton>>();
            keyboard.add(List.of(new InlineKeyboardButton().setText("Меню").setCallbackData(GET_MENU.getValue())));
            message.setReplyMarkup(new InlineKeyboardMarkup(keyboard));
            return message;
        }

        val keyboard = new ArrayList<List<InlineKeyboardButton>>();
        plannedTrainingList.forEach(training -> keyboard.add(
                List.of(new InlineKeyboardButton()
                        .setText(training.getName())
                        .setCallbackData(START_TRAINING.getValue() + "" + training.getId())
                )
        ));
        keyboard.add(List.of(new InlineKeyboardButton().setText("Меню").setCallbackData(GET_MENU.getValue())));

        val message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Какую тренировку стартуем?");
        message.setReplyMarkup(new InlineKeyboardMarkup(keyboard));
        return message;
    }

    @NotNull
    @Override
    public Command commandToHandle() {
        return Command.CHOOSE_TRAINING_TO_START;
    }
}
