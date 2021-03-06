package com.narryel.fitness.bot.handlers.command.update_handlers;

import com.narryel.fitness.bot.handlers.UpdateHandler;
import com.narryel.fitness.domain.entity.UserState;
import com.narryel.fitness.repository.UserStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.objects.Reply;

import static com.narryel.fitness.domain.enums.Command.ADD_EXERCISE;
import static com.narryel.fitness.domain.enums.State.WAITING_FOR_EXERCISE_NAME;
import static com.narryel.fitness.util.UpdateCheckUtils.callbackDataContains;

@Service
@RequiredArgsConstructor
public class AddExerciseCommandHandler implements UpdateHandler {

    private final UserStateRepository stateRepository;

    @Override
    public Reply getRespondingReply() {
        return Reply.of((bot, update) -> {
                    val chatId = update.getCallbackQuery().getMessage().getChatId();
                    bot.silent().send("Введите название упражнения", chatId);
                    val trainingId = Long.valueOf(
                            update.getCallbackQuery().getData()
                                    .replace(ADD_EXERCISE.getValue() + "", "")
                    );
                    stateRepository.save(new UserState().setState(WAITING_FOR_EXERCISE_NAME).setChatId(chatId).setTrainingId(trainingId));
                },
                callbackDataContains(ADD_EXERCISE));

    }
}
