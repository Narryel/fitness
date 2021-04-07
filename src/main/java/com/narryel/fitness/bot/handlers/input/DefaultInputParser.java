package com.narryel.fitness.bot.handlers.input;

import com.narryel.fitness.repository.UserStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultInputParser {
    private final UserStateRepository stateRepository;
    private final UserInputHandlerFactory userInputHandlerFactory;

    public void handleUserInput(AbilityBot bot, MessageContext ctx) {
        val chatId = ctx.chatId();
        val update = ctx.update();

        val optional = stateRepository.findByChatId(chatId);
        if (optional.isPresent()) {
            val handler = userInputHandlerFactory
                    .getHandlerByState(optional.get().getState());
            val validationResult = handler.checkInputValidity(update);
            SendMessage responseMessage;
            if (validationResult.isMessageValid()) {
                responseMessage = handler.handleUserInput(update);
            } else {
                responseMessage = new SendMessage();
                responseMessage.setChatId(chatId.toString());
                responseMessage.setText(validationResult.getMessage());
            }
            bot.silent().execute(responseMessage);

        } else {
            if (update.hasMessage()) {
                log.error("can't parse user request. Request text: {}", update.getMessage().getText());
            }
            if (update.hasCallbackQuery()) {
                log.error("can't parse user request. Request callbackquery data: {}", update.getCallbackQuery().getData());
            }
            //TODO add menu button + finish all stuff
            bot.silent().send("не понимать", chatId);
        }
    }
}
