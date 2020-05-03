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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static com.narryel.fitness.domain.enums.Command.*;

@Service
@RequiredArgsConstructor
public class StartTrainingPlanningCommandHandler implements CommandHandler {

    private final FitUserRepository userRepository;
    private final TrainingRepository trainingRepository;

    @Override
    public SendMessage handleCommand(Long chatId) {

        final var message = new SendMessage();
        final var fitUser = userRepository.findByChatId(chatId)
                .orElseThrow(() -> new EntityNotFoundException("chatId", chatId.toString(), FitUser.class));


        final var plannedTrainingList = trainingRepository.findByUserAndStatus(fitUser, TrainingStatus.READY);
        final var keyboard = new ArrayList<List<InlineKeyboardButton>>();

        plannedTrainingList.forEach(training -> keyboard.add(
                List.of(new InlineKeyboardButton()
                        //todo Add training names
                        .setText(training.getId().toString())
                        .setCallbackData(START_TRAINING.name() + " " + training.getId())
                )
        ));
        keyboard.add(List.of(new InlineKeyboardButton().setText("Меню").setCallbackData(GET_MENU_CMD.getValue())));

        message.setText("Какую тренировку стартуем?");
        message.setReplyMarkup(new InlineKeyboardMarkup(keyboard));
        message.setChatId(chatId);
        return message;
    }

    @Override
    public Command commandToHandle() {
        return Command.CHOOSE_TRAINING_TO_START_CMD;
    }
}
