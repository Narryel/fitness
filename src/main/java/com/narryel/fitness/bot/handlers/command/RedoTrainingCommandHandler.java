package com.narryel.fitness.bot.handlers.command;

import com.narryel.fitness.domain.entity.Exercise;
import com.narryel.fitness.domain.entity.Training;
import com.narryel.fitness.domain.enums.Command;
import com.narryel.fitness.domain.enums.TrainingStatus;
import com.narryel.fitness.exceptions.EntityNotFoundException;
import com.narryel.fitness.repository.TrainingRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.stream.Collectors;

import static com.narryel.fitness.util.MessageGenerator.generateOnlyMenuInlineKeyboard;

@Service
@RequiredArgsConstructor
public class RedoTrainingCommandHandler extends CommandHandler {

    private final TrainingRepository trainingRepository;

    @Override
    @Transactional
    public SendMessage handleCommand(Update update) {
        val trainingId = getEntityIdFromUpdate(update);
        val training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new EntityNotFoundException(trainingId, Training.class));
        val newTraining = new Training()
                .setName(training.getName())
                .setStatus(TrainingStatus.READY)
                .setUser(training.getUser());

        val newExerciseList = training.getExercises().stream()
                .map(exercise -> new Exercise()
                        .setName(exercise.getName())
                        .setTraining(newTraining)
                        .setStatus(TrainingStatus.READY)
                ).collect(Collectors.toList());

        newTraining.setExercises(newExerciseList);
        trainingRepository.save(newTraining);

        val sendMessage = new SendMessage(getChatId(update).toString(), "Тренировка запланирована!");
        sendMessage.setReplyMarkup(generateOnlyMenuInlineKeyboard());
        return sendMessage;
    }

    @NotNull
    @Override
    public Command commandToHandle() {
        return Command.REDO_TRAINING;
    }

//    @Override
//    public Reply getRespondingReply() {
//        return Reply.of(
//                (bot, update) -> {
//                    val sendMessage = handleCommand(update);
//                    bot.silent().execute(sendMessage);
//                },
//                callbackDataContains(REDO_TRAINING)
//        );
//
//    }
}
