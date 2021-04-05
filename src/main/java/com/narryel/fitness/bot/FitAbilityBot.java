package com.narryel.fitness.bot;

import com.narryel.fitness.bot.handlers.UpdateHandler;
import com.narryel.fitness.bot.handlers.input.UserInputHandlerFactory;
import com.narryel.fitness.configuration.properties.AbilityBotCredentials;
import com.narryel.fitness.domain.entity.UserState;
import com.narryel.fitness.exceptions.EntityNotFoundException;
import com.narryel.fitness.repository.UserStateRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.abilitybots.api.objects.ReplyCollection;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.narryel.fitness.domain.enums.Command.*;
import static com.narryel.fitness.domain.enums.State.*;
import static com.narryel.fitness.util.UpdateCheckUtils.callbackDataContains;
import static com.narryel.fitness.util.UpdateCheckUtils.callbackDataEquals;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

@Slf4j
@Component
public class FitAbilityBot extends AbilityBot {

    private final UserStateRepository stateRepository;
    private final AbilityBotCredentials credentials;
    private final UserInputHandlerFactory userInputHandlerFactory;
    private final List<UpdateHandler> updateHandlers;

    @Autowired
    public FitAbilityBot(AbilityBotCredentials credentials,
                         UserStateRepository stateRepository,
                         UserInputHandlerFactory userInputHandlerFactory,
                         List<UpdateHandler> updateHandlers
    ) {

        super(credentials.getToken(), credentials.getUserName());
        this.credentials = credentials;
        this.stateRepository = stateRepository;
        this.userInputHandlerFactory = userInputHandlerFactory;
        this.updateHandlers = updateHandlers;
    }

    @Override
    public long creatorId() {
        return credentials.getCreatorId();
    }

    //TODO extract to
    public Ability registerUser() {
        return Ability
                .builder()
                .flag(Flag.CALLBACK_QUERY)
                .name(REGISTER_USER.getValue())
                .info("register user")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> {
                    silent.send("Введите ваш никнейм", ctx.chatId());
                    stateRepository.save(new UserState().setState(WAITING_FOR_USER_NICKNAME).setChatId(ctx.chatId()));
                })

                .reply((bot, update) -> {
                    val chatId = update.getCallbackQuery().getMessage().getChatId();
                    silent.send("Введите ваш никнейм", chatId);
                    stateRepository.save(new UserState().setState(WAITING_FOR_USER_NICKNAME).setChatId(chatId));
                }, callbackDataEquals(REGISTER_USER))
                .build();
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
                updateHandlers
                        .stream()
                        .map(UpdateHandler::getRespondingReply)
                        .collect(Collectors.toList()
                        )
        );
    }

    public Reply planTrainingNameReply() {
        Consumer<Update> action = upd -> {
            val chatId = upd.getCallbackQuery().getMessage().getChatId();
            silent.send("Как назовем тренировку?", chatId);
            stateRepository.save(new UserState().setState(WAITING_FOR_TRAINING_NAME).setChatId(chatId));
        };
        return Reply.of(action, callbackDataEquals(PLAN_TRAINING));
    }

    public Reply addExerciseReply() {
        Consumer<Update> action = upd -> {
            val chatId = upd.getCallbackQuery().getMessage().getChatId();
            silent.send("Введите название упражнения", chatId);
            val trainingId = Long.valueOf(
                    upd.getCallbackQuery().getData()
                            .replace(ADD_EXERCISE.getValue() + "", "")
            );
            stateRepository.save(new UserState().setState(WAITING_FOR_EXERCISE_NAME).setChatId(chatId).setTrainingId(trainingId));
        };
        return Reply.of(action, callbackDataContains(ADD_EXERCISE));
    }

    /**
     * прокидываем ExerciseId через state, чтобы зафиксировать вес у упражнения
     */
    public Reply startExercise() {
        Consumer<Update> action = upd -> {
            val chatId = upd.getCallbackQuery().getMessage().getChatId();
            val exerciseId = Long.valueOf(
                    upd.getCallbackQuery().getData()
                            .replace(START_EXERCISE.getValue() + " ", "")
            );
            stateRepository.save(new UserState().setState(WAITING_FOR_WEIGHT).setChatId(chatId).setExerciseId(exerciseId));
            silent.send("Введите вес, с которым будете заниматься\nЕсли предполагается вес тела - введите 0", chatId);
        };
        return Reply.of(action, callbackDataContains(START_EXERCISE));
    }

    public Reply changeWeight() {
        Consumer<Update> action = upd -> {
            val chatId = upd.getCallbackQuery().getMessage().getChatId();
            val state = stateRepository.findByChatId(chatId)
                    .orElseThrow(() -> new EntityNotFoundException("chatId", chatId, UserState.class));
            state.setState(WAITING_FOR_WEIGHT);
            stateRepository.save(state);

            silent.send("Введите новый вес", chatId);
        };
        return Reply.of(action, callbackDataContains(CHANGE_WEIGHT));
    }

}
