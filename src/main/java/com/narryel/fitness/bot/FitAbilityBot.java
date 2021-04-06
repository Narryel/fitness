package com.narryel.fitness.bot;

import com.narryel.fitness.bot.handlers.UpdateHandler;
import com.narryel.fitness.bot.handlers.input.UserInputHandlerFactory;
import com.narryel.fitness.configuration.properties.AbilityBotCredentials;
import com.narryel.fitness.repository.UserStateRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.objects.ReplyCollection;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.stream.Collectors;

import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

@Slf4j
@Component
public class FitAbilityBot extends AbilityBot {

    private final UserStateRepository stateRepository;
    private final AbilityBotCredentials credentials;
    private final UserInputHandlerFactory userInputHandlerFactory;
    private final List<UpdateHandler> updateHandlerList;

    @Autowired
    public FitAbilityBot(AbilityBotCredentials credentials,
                         UserStateRepository stateRepository,
                         UserInputHandlerFactory userInputHandlerFactory,
                         List<UpdateHandler> updateHandlerList
    ) {

        super(credentials.getToken(), credentials.getUserName());
        this.credentials = credentials;
        this.stateRepository = stateRepository;
        this.userInputHandlerFactory = userInputHandlerFactory;
        this.updateHandlerList = updateHandlerList;
    }

    @Override
    public long creatorId() {
        return credentials.getCreatorId();
    }


    //todo refactor
    public Ability readUserInput() {
        return Ability
                .builder()
                .name(DEFAULT)
                .info("userInput")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(this::handleUserInput)
                .build();
    }

    private void handleUserInput(MessageContext ctx) {
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
            silent.execute(responseMessage);

        } else {
            if (update.hasMessage()) {
                log.error("can't parse user request. Request text: {}", update.getMessage().getText());
            }
            if (update.hasCallbackQuery()) {
                log.error("can't parse user request. Request callbackquery data: {}", update.getCallbackQuery().getData());
            }
            //TODO add menu button + finish all stuff
            silent.send("не понимать", chatId);
        }
    }

    public ReplyCollection registerAllRepliesFromHandlers() {
        return new ReplyCollection(
                updateHandlerList
                        .stream()
                        .map(UpdateHandler::getRespondingReply)
                        .collect(Collectors.toList()
                        )
        );
    }
}
