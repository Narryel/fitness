package com.narryel.fitness.bot;

import com.narryel.fitness.bot.handlers.command.CommandHandlerFactory;
import com.narryel.fitness.bot.handlers.input.UserInputHandlerFactory;
import com.narryel.fitness.configuration.properties.AbilityBotCredentials;
import com.narryel.fitness.domain.entity.UserState;
import com.narryel.fitness.domain.enums.Command;
import com.narryel.fitness.exceptions.EntityNotFoundException;
import com.narryel.fitness.repository.UserStateRepository;
import com.narryel.fitness.util.MessageGenerator;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.narryel.fitness.domain.enums.Command.*;
import static com.narryel.fitness.domain.enums.State.*;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

@Slf4j
@Component
@EnableScheduling
public class FitAbilityBot extends AbilityBot {

    private final UserStateRepository stateRepository;
    private final AbilityBotCredentials credentials;
    private final UserInputHandlerFactory userInputHandlerFactory;
    private final CommandHandlerFactory commandHandlerFactory;
    private final MessageGenerator messageGenerator;

    @Autowired
    public FitAbilityBot( AbilityBotCredentials credentials,
                         UserStateRepository stateRepository,
                         UserInputHandlerFactory userInputHandlerFactory,
                         CommandHandlerFactory commandHandlerFactory,
                         MessageGenerator messageGenerator
    ) {

        super(credentials.getToken(), credentials.getUserName());
        this.credentials = credentials;
        this.stateRepository = stateRepository;
        this.userInputHandlerFactory = userInputHandlerFactory;
        this.commandHandlerFactory = commandHandlerFactory;
        this.messageGenerator = messageGenerator;
    }

    @Override
    public int creatorId() {
        return credentials.getCreatorId();
    }

    public Ability clearState() {
        return Ability
                .builder()
                .name("clearstate")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(messageContext -> {
                    stateRepository.deleteByChatId(messageContext.chatId());
                    silent.send("стейт почищен", messageContext.chatId());
                })
                .build();

    }

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
                .reply(ctx -> {
                    val chatId = ctx.getCallbackQuery().getMessage().getChatId();
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
                .action(ctx -> {
                    val optional = stateRepository.findByChatId(ctx.chatId());
                    if (optional.isPresent()) {
                        val handler = userInputHandlerFactory
                                .getHandlerByState(optional.get().getState());
                        val validationResult = handler.checkInputValidity(ctx.update());
                        SendMessage responseMessage;
                        if (validationResult.isMessageValid()) {
                            responseMessage = handler.handle(ctx.update());
                        } else {
                            responseMessage = new SendMessage()
                                    .setChatId(ctx.chatId())
                                    .setText(validationResult.getMessage());
                        }
                        silent.execute(responseMessage);

                    } else {
                        if (ctx.update().hasMessage()) {
                            log.error("can't parse user request. Request text: {}", ctx.update().getMessage().getText());
                        }
                        if (ctx.update().hasCallbackQuery()) {
                            log.error("can't parse user request. Request callbackquery data: {}", ctx.update().getCallbackQuery().getData());
                        }
                        silent.send("не понимать", ctx.chatId());

                    }
                })
                .build();
    }

    public Reply start() {
        Consumer<Update> action = upd -> {
            val message = commandHandlerFactory.getHandlerByCommand(START).handleCommand(upd);
            silent.execute(message);
        };
        return Reply.of(action, textEquals(START));
    }

    public Reply getMenu() {
        Consumer<Update> action = upd -> {
            val chatId = upd.getCallbackQuery().getMessage().getChatId();
            silent.execute(messageGenerator.getMenu(chatId));
        };
        return Reply.of(action, callbackDataEquals(GET_MENU));
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

    public Reply editExercise() {
        Consumer<Update> action = upd -> {
            val sendMessage = commandHandlerFactory.getHandlerByCommand(EDIT_EXERCISE).handleCommand(upd);
            silent.execute(sendMessage);
        };
        return Reply.of(action, callbackDataContains(EDIT_EXERCISE));
    }

    public Reply finishTrainingPlanning() {
        Consumer<Update> action = upd -> {
            val message = commandHandlerFactory.getHandlerByCommand(FINISH_TRAINING_PLANNING).handleCommand(upd);
            silent.execute(message);
        };
        return Reply.of(action, callbackDataEquals(FINISH_TRAINING_PLANNING));
    }

    public Reply chooseTrainingToStart() {
        Consumer<Update> action = upd -> {
            val sendMessage = commandHandlerFactory.getHandlerByCommand(CHOOSE_TRAINING_TO_START).handleCommand(upd);
            silent.execute(sendMessage);
        };
        return Reply.of(action, callbackDataEquals(CHOOSE_TRAINING_TO_START));
    }

    public Reply startTraining() {
        Consumer<Update> action = upd -> {
            val sendMessage = commandHandlerFactory.getHandlerByCommand(START_TRAINING).handleCommand(upd);
            silent.execute(sendMessage);
        };
        return Reply.of(action, callbackDataContains(START_TRAINING));
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

    public Reply finishExercise() {
        Consumer<Update> action = upd -> {
            val sendMessage = commandHandlerFactory.getHandlerByCommand(FINISH_EXERCISE).handleCommand(upd);
            silent.execute(sendMessage);
        };
        return Reply.of(action, callbackDataContains(FINISH_EXERCISE));
    }

    public Reply finishTraining() {
        Consumer<Update> action = upd -> {
            val sendMessage = commandHandlerFactory.getHandlerByCommand(FINISH_TRAINING).handleCommand(upd);
            silent.execute(sendMessage);
        };
        return Reply.of(action, callbackDataContains(FINISH_TRAINING));
    }

    public Reply openTrainingHistory() {
        Consumer<Update> action = upd -> {
            val sendMessage = commandHandlerFactory.getHandlerByCommand(TRAINING_HISTORY).handleCommand(upd);
            silent.execute(sendMessage);
        };
        return Reply.of(action, callbackDataEquals(TRAINING_HISTORY));
    }

    public Reply viewFinishedTraining() {
        return Reply.of(
                upd -> {
                    val sendMessage = commandHandlerFactory.getHandlerByCommand(VIEW_FINISHED_TRAINING).handleCommand(upd);
                    silent.execute(sendMessage);
                },
                callbackDataContains(VIEW_FINISHED_TRAINING)
        );
    }

    public Reply redoFinishedTraining() {
        return Reply.of(
                update -> {
                    val sendMessage = commandHandlerFactory.getHandlerByCommand(REDO_TRAINING).handleCommand(update);
                    silent.execute(sendMessage);
                },
                callbackDataContains(REDO_TRAINING)
        );
    }

    public Reply deleteTrainingFromHistory() {
        return Reply.of(
                update -> {
                    val sendMessage = commandHandlerFactory.getHandlerByCommand(DELETE_TRAINING_FROM_HISTORY).handleCommand(update);
                    silent.execute(sendMessage);
                },
                callbackDataContains(DELETE_TRAINING_FROM_HISTORY)
        );
    }

    //TODO scheduled task to dump all user states and training statuses


    private Predicate<Update> callbackDataEquals(Command command) {
        return update -> {
            CallbackQuery callbackData = update.getCallbackQuery();
            if (callbackData == null) {
                return false;
            }
            return command.getValue().equals(callbackData.getData());
        };
    }

    private Predicate<Update> callbackDataContains(Command command) {
        return update -> {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            if (callbackQuery == null) {
                return false;
            }
            return callbackQuery.getData().contains(command.getValue());
        };
    }

    private Predicate<Update> textEquals(Command command) {
        return update -> {
            val message = update.getMessage();
            if (message == null) {
                return false;
            }
            if (!message.hasText()) {
                return false;
            }
            return command.getValue().equals(message.getText());
        };
    }
}
