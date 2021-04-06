package com.narryel.fitness.bot.handlers.command.update_handlers;

import com.narryel.fitness.bot.handlers.UpdateHandler;
import com.narryel.fitness.domain.entity.UserState;
import com.narryel.fitness.repository.UserStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.objects.Reply;

import static com.narryel.fitness.domain.enums.Command.REGISTER_USER;
import static com.narryel.fitness.domain.enums.State.WAITING_FOR_USER_NICKNAME;
import static com.narryel.fitness.util.UpdateCheckUtils.callbackDataEquals;

@Service
@RequiredArgsConstructor
public class RegisterUserCommandHandler implements UpdateHandler {
    private final UserStateRepository stateRepository;


    @Override
    public Reply getRespondingReply() {
        return Reply.of((bot, update) -> {
                    val chatId = update.getCallbackQuery().getMessage().getChatId();
                    bot.silent().send("Введите ваш никнейм", chatId);
                    stateRepository.save(new UserState().setState(WAITING_FOR_USER_NICKNAME).setChatId(chatId));
                },
                callbackDataEquals(REGISTER_USER));
    }
}
