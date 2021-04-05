package com.narryel.fitness.bot.handlers.command;

import com.narryel.fitness.domain.entity.Training;
import com.narryel.fitness.domain.enums.Command;
import com.narryel.fitness.exceptions.EntityNotFoundException;
import com.narryel.fitness.repository.FitUserRepository;
import com.narryel.fitness.repository.TrainingRepository;
import com.narryel.fitness.util.MessageGenerator;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Objects;

import static com.narryel.fitness.domain.enums.Command.FINISH_EXERCISE;
import static com.narryel.fitness.domain.enums.Command.FINISH_TRAINING_PLANNING;
import static com.narryel.fitness.domain.enums.TrainingStatus.READY;

@Service
@RequiredArgsConstructor
public class FinishTrainingPlanningCommandHandler extends CommandHandler {
    private final TrainingRepository trainingRepository;
    private final MessageGenerator messageGenerator;

    @Override
    public SendMessage handleCommand(Update update) {
        val chatId = getChatId(update);
        val exerciseId = extractIdFromCallbackQuery(update);
        val training = trainingRepository.findById(exerciseId).orElseThrow(() -> new EntityNotFoundException(exerciseId, Training.class));
        training.setStatus(READY);
        trainingRepository.save(training);
        return messageGenerator.getMenu(Long.valueOf(chatId));
    }

    @NotNull
    @Override
    public Command commandToHandle() {
        return FINISH_TRAINING_PLANNING;
    }

}
