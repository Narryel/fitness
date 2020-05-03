package com.narryel.fitness.bot.handlers.input;

import com.narryel.fitness.dao.UserService;
import com.narryel.fitness.domain.entity.FitUser;
import com.narryel.fitness.domain.enums.State;
import com.narryel.fitness.domain.enums.UserStatus;
import com.narryel.fitness.util.MessageGenerators;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.narryel.fitness.domain.enums.State.WAITING_FOR_USER_NICKNAME;

@Service
@RequiredArgsConstructor
public class NicknameInputHandler implements UserInputHandler {

    private final UserService userService;

    @Override
    public SendMessage handle(Update update) {

        final var fitUser = new FitUser()
                .setChatId(update.getMessage().getChatId())
                .setNickName(update.getMessage().getText())
                .setTelegramUserId(update.getMessage().getFrom().getId())
                .setStatus(UserStatus.ACTIVE);

        userService.upsertUser(fitUser);


        return MessageGenerators.getMenu(update.getMessage().getChatId());

    }

    @Override
    public State stateToHandle() {
        return WAITING_FOR_USER_NICKNAME;
    }
}
