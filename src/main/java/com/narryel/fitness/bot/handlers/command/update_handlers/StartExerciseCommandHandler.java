package com.narryel.fitness.bot.handlers.command.update_handlers;

import com.narryel.fitness.bot.handlers.UpdateHandler;
import com.narryel.fitness.domain.entity.UserState;
import com.narryel.fitness.repository.UserStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.objects.Reply;

import static com.narryel.fitness.domain.enums.Command.START_EXERCISE;
import static com.narryel.fitness.domain.enums.State.WAITING_FOR_WEIGHT;
import static com.narryel.fitness.util.UpdateCheckUtils.callbackDataContains;

@Service
@RequiredArgsConstructor
public class StartExerciseCommandHandler implements UpdateHandler {
    private final UserStateRepository stateRepository;

    @Override
    public Reply getRespondingReply() {
        return Reply.of((bot, update) -> {
            val chatId = update.getCallbackQuery().getMessage().getChatId();
            val exerciseId = Long.valueOf(
                    update.getCallbackQuery().getData()
                            .replace(START_EXERCISE.getValue(), "")
            );
            stateRepository.save(new UserState().setState(WAITING_FOR_WEIGHT).setChatId(chatId).setExerciseId(exerciseId));
            bot.silent().send("Введите вес, с которым будете заниматься\nЕсли предполагается вес тела - введите 0", chatId);
        },
        callbackDataContains(START_EXERCISE));

    }
}
