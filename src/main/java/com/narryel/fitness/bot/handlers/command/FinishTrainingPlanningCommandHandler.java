package com.narryel.fitness.bot.handlers.command;

import com.narryel.fitness.domain.enums.Command;
import com.narryel.fitness.exceptions.EntityNotFoundException;
import com.narryel.fitness.repository.FitUserRepository;
import com.narryel.fitness.repository.TrainingRepository;
import com.narryel.fitness.util.MessageGenerator;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Objects;

import static com.narryel.fitness.domain.enums.Command.FINISH_TRAINING_PLANNING;

@Service
@RequiredArgsConstructor
public class FinishTrainingPlanningCommandHandler extends CommandHandler {
    private final FitUserRepository fitUserRepository;
    private final TrainingRepository trainingRepository;
    private final MessageGenerator messageGenerator;

    @Override
    @Transactional
    public SendMessage handleCommand(Update update) {
        final var chatId = Objects.requireNonNull(update.getCallbackQuery().getMessage().getChatId());
        final var user = fitUserRepository.findByChatId(chatId)
                .orElseThrow(EntityNotFoundException::new);
        trainingRepository.finishPlanningUserTraining(user);
        return messageGenerator.getMenu(chatId);
    }

    @NotNull
    @Override
    public Command commandToHandle() {
        return FINISH_TRAINING_PLANNING;
    }


//    @Override
//    public Reply getRespondingReply() {
//        return Reply.of(
//                (bot, upd) -> {
//                    val message = handleCommand(upd);
//                    bot.silent().execute(message);
//                },
//                callbackDataEquals(FINISH_TRAINING_PLANNING));
//    }
}
