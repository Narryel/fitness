package com.narryel.fitness.bot.handlers.command.update_handlers;

import com.narryel.fitness.bot.handlers.UpdateHandler;
import com.narryel.fitness.domain.entity.UserState;
import com.narryel.fitness.repository.UserStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.objects.Reply;

import static com.narryel.fitness.domain.enums.Command.PLAN_TRAINING;
import static com.narryel.fitness.domain.enums.State.WAITING_FOR_TRAINING_NAME;
import static com.narryel.fitness.util.UpdateCheckUtils.callbackDataEquals;

@Service
@RequiredArgsConstructor
public class PlanTrainingCommandHandler implements UpdateHandler {

    private final UserStateRepository stateRepository;

    @Override
    public Reply getRespondingReply() {
        return Reply.of(
                (bot, upd) -> {
                    val chatId = upd.getCallbackQuery().getMessage().getChatId();
                    bot.silent().send("Как назовем тренировку?", chatId);
                    stateRepository.save(new UserState().setState(WAITING_FOR_TRAINING_NAME).setChatId(chatId));
                },
                callbackDataEquals(PLAN_TRAINING));
    }
}
