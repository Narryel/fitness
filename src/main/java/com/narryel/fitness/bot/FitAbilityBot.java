package com.narryel.fitness.bot;

import com.narryel.fitness.bot.handlers.UpdateHandler;
import com.narryel.fitness.bot.handlers.input.DefaultInputParser;
import com.narryel.fitness.configuration.properties.AbilityBotCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.ReplyCollection;

import java.util.List;
import java.util.stream.Collectors;

import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

@Slf4j
@Component
public class FitAbilityBot extends AbilityBot {

    private final AbilityBotCredentials credentials;
    private final List<UpdateHandler> updateHandlerList;
    private final DefaultInputParser defaultInputParser;

    @Autowired
    public FitAbilityBot(AbilityBotCredentials credentials,
                         List<UpdateHandler> updateHandlerList,
                         DefaultInputParser defaultInputParser) {

        super(credentials.getToken(), credentials.getUserName());
        this.credentials = credentials;
        this.updateHandlerList = updateHandlerList;
        this.defaultInputParser = defaultInputParser;
    }

    @Override
    public long creatorId() {
        return credentials.getCreatorId();
    }


    public Ability readUserInput() {
        return Ability
                .builder()
                .name(DEFAULT)
                .info("userInput")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> defaultInputParser.handleUserInput(this, ctx))
                .build();
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
