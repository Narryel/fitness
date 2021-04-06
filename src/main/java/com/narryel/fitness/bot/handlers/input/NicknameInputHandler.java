package com.narryel.fitness.bot.handlers.input;

import com.narryel.fitness.domain.entity.FitUser;
import com.narryel.fitness.domain.enums.State;
import com.narryel.fitness.domain.enums.UserStatus;
import com.narryel.fitness.repository.FitUserRepository;
import com.narryel.fitness.repository.UserStateRepository;
import com.narryel.fitness.util.MessageGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.narryel.fitness.domain.enums.State.WAITING_FOR_USER_NICKNAME;

@Service
@RequiredArgsConstructor
public class NicknameInputHandler implements UserInputHandler {
    private final MessageGenerator messageGenerator;
    private final FitUserRepository userRepository;
    private final UserStateRepository stateRepository;

    @Override
    @Transactional
    public SendMessage handleUserInput(Update update) {
        stateRepository.deleteByChatId(update.getMessage().getChatId());

        final var fitUser = new FitUser()
                .setChatId(update.getMessage().getChatId())
                .setNickName(update.getMessage().getText())
                .setTelegramUserId(update.getMessage().getFrom().getId())
                .setStatus(UserStatus.ACTIVE);

        userRepository.save(fitUser);


        return messageGenerator.getMenu(update.getMessage().getChatId());

    }

    @Override
    public State stateToHandle() {
        return WAITING_FOR_USER_NICKNAME;
    }
}
