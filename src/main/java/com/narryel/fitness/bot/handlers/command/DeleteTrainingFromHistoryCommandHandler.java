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

import static com.narryel.fitness.domain.enums.Command.DELETE_TRAINING_FROM_HISTORY;

@Service
@RequiredArgsConstructor
public class DeleteTrainingFromHistoryCommandHandler implements CommandHandler {

    private final TrainingRepository trainingRepository;
    private final FitUserRepository userRepository;
    private final TrainingHistoryCommandHandler trainingHistoryCommandHandler;

    @Override
    public SendMessage handleCommand(Update update) {
        val chatId = getChatId(update);
        val trainingId = Long.valueOf(getData(update).replace(this.commandToHandle().getValue(), ""));
        val training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new EntityNotFoundException(trainingId, Training.class));

        trainingRepository.save(training.setStatus(TrainingStatus.HIDDEN));

        val user = userRepository.findByChatId(chatId)
                .orElseThrow(() -> new EntityNotFoundException("chatId", chatId, FitUser.class));

        val trainingList = trainingRepository.findByUserAndStatus(user, TrainingStatus.FINISHED);
        val message = trainingList.isEmpty() ? "Тренировка удалена. История тренировок пуста." :
                "Тренировка удалена. Какую тренировку будем вспоминать?";

        val trainingHistoryKeyboard = trainingHistoryCommandHandler.getTrainingHistoryKeyboard(trainingList);

        val sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(trainingHistoryKeyboard);
        return sendMessage;
    }

    @NotNull
    @Override
    public Command commandToHandle() {
        return DELETE_TRAINING_FROM_HISTORY;
    }
}
