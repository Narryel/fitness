package com.narryel.fitness.bot;

import com.narryel.fitness.bot.handlers.command.CommandHandlerFactory;
import com.narryel.fitness.bot.handlers.input.UserInputHandlerFactory;
import com.narryel.fitness.configuration.properties.AbilityBotCredentials;
import com.narryel.fitness.domain.entity.UserState;
import com.narryel.fitness.domain.enums.Command;
import com.narryel.fitness.repository.UserStateRepository;
import com.narryel.fitness.util.MessageGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.narryel.fitness.domain.enums.Command.*;
import static com.narryel.fitness.domain.enums.State.*;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

@Slf4j
@Component
public class FitAbilityBot extends AbilityBot {

    private UserStateRepository stateRepository;
    private AbilityBotCredentials credentials;
    private UserInputHandlerFactory userInputHandlerFactory;
    private CommandHandlerFactory commandHandlerFactory;
    private MessageGenerator messageGenerator;


    private static final String TOKEN = "dfghjklgfdghj";
    private static final String USER_NAME = "hjkjhghgjhjlkjhgh";

    @Autowired
    public FitAbilityBot(AbilityBotCredentials credentials,
                         UserStateRepository stateRepository,
                         UserInputHandlerFactory userInputHandlerFactory,
                         CommandHandlerFactory commandHandlerFactory,
                         MessageGenerator messageGenerator) {
        //FIXME proper spring injection
        this(TOKEN, USER_NAME);
        this.credentials = credentials;
        this.stateRepository = stateRepository;
        this.userInputHandlerFactory = userInputHandlerFactory;
        this.commandHandlerFactory = commandHandlerFactory;
        this.messageGenerator = messageGenerator;
    }

    protected FitAbilityBot(String botToken, String botUsername) {
        super(botToken, botUsername);
    }

    @Override
    public int creatorId() {
        return credentials.getCreatorId();
    }

    public Ability clearState() {
        return Ability
                .builder()
                .name("clearState")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(messageContext -> {
                    stateRepository.deleteByChatId(messageContext.chatId());
                    silent.send("стейт почищен", messageContext.chatId());
                })
                .build();

    }

    public Ability sayHelloWorld() {
        return Ability
                .builder()
                .name("start")
                .info("register")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> {
                            final var inlineKeyboardMarkup = new InlineKeyboardMarkup();
                            final var button1 = new InlineKeyboardButton();
                            button1.setText("Зарегистрироваться");
                            button1.setCallbackData(REGISTER_USER.getValue());
//                            final var button2 = new InlineKeyboardButton();
//                            button2.setText("кто я");
//                            button2.setCallbackData("me");
                            inlineKeyboardMarkup.setKeyboard(List.of(List.of(button1)));

                            var message = new SendMessage() // Create a SendMessage object with mandatory fields
                                    .setChatId(ctx.update().getMessage().getChatId())
                                    .setText("Новенький у нас? зарегистрируемся?")
                                    .setReplyMarkup(inlineKeyboardMarkup);


                            try {
                                execute(message);
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                        }
                )
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
                    final var chatId = ctx.getCallbackQuery().getMessage().getChatId();
                    silent.send("Введите ваш никнейм", chatId);
                    stateRepository.save(new UserState().setState(WAITING_FOR_USER_NICKNAME).setChatId(chatId));
                }, callbackDataEquals(REGISTER_USER))
                .build();
    }


    public Ability readUserInput() {
        return Ability
                .builder()
                .name(DEFAULT)
                .info("userInput")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> {
                    final var optional = stateRepository.findByChatId(ctx.chatId());
                    if (optional.isPresent()) {
                        final var message = userInputHandlerFactory
                                .getHandler(optional.get().getState())
                                .handle(ctx.update());
                        silent.execute(message);
//                        stateRepository.delete(optional.get());
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

    public Reply planTrainingReply() {
        Consumer<Update> action = upd -> {
            final var chatId = upd.getCallbackQuery().getMessage().getChatId();
            silent.send("Введите название первого упражнение", chatId);
            stateRepository.save(new UserState().setState(WAITING_FOR_EXERCISE_NAME).setChatId(chatId));
        };
        return Reply.of(action, callbackDataEquals(PLAN_TRAINING));
    }

    public Reply addExerciseReply() {
        Consumer<Update> action = upd -> {
            final var chatId = upd.getCallbackQuery().getMessage().getChatId();
            silent.send("Введите название упражнения", chatId);
            stateRepository.save(new UserState().setState(WAITING_FOR_EXERCISE_NAME).setChatId(chatId));
        };
        return Reply.of(action, callbackDataEquals(ADD_EXERCISE));
    }

    public Reply getMenu() {
        Consumer<Update> action = upd -> {
            final var chatId = upd.getCallbackQuery().getMessage().getChatId();
            silent.execute(messageGenerator.getMenu(chatId));
        };
        return Reply.of(action, callbackDataEquals(GET_MENU));
    }

    public Reply finishTrainingPlanning() {
        Consumer<Update> action = upd -> {
            final var message = commandHandlerFactory.getHandler(FINISH_TRAINING_PLANNING).handleCommand(upd);
            silent.execute(message);
        };
        return Reply.of(action, callbackDataEquals(FINISH_TRAINING_PLANNING));
    }

    public Reply chooseTrainingToStart() {
        Consumer<Update> action = upd -> {
            final var sendMessage = commandHandlerFactory.getHandler(CHOOSE_TRAINING_TO_START).handleCommand(upd);
            silent.execute(sendMessage);
        };
        return Reply.of(action, callbackDataEquals(CHOOSE_TRAINING_TO_START));
    }

    public Reply startTraining() {
        Consumer<Update> action = upd -> {
            final var sendMessage = commandHandlerFactory.getHandler(START_TRAINING).handleCommand(upd);
            silent.execute(sendMessage);
        };
        return Reply.of(action, callbackDataContains(START_TRAINING));
    }


    //todo попробовать сюда же запихнуть изменение веса
    /**
     * прокидываем ExerciseId через state, чтобы зафиксировать вес у упражнения
     */
    public Reply startExercise() {
        Consumer<Update> action = upd -> {
            final var chatId = upd.getCallbackQuery().getMessage().getChatId();
            final var exerciseId = Long.valueOf(
                    upd.getCallbackQuery().getData()
                            .replace(START_EXERCISE.getValue() + " ", "")
            );
            stateRepository.save(new UserState().setState(WAITING_FOR_WEIGHT).setChatId(chatId).setExerciseId(exerciseId));
            silent.send("Введите вес, с которым будете заниматься", chatId);
        };
        return Reply.of(action, callbackDataContains(START_EXERCISE));
    }

    public Reply finishExercise() {
        Consumer<Update> action = upd -> {
            final var sendMessage = commandHandlerFactory.getHandler(FINISH_EXERCISE).handleCommand(upd);
            silent.execute(sendMessage);
        };
        return Reply.of(action, callbackDataEquals(FINISH_EXERCISE));
    }




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

}
