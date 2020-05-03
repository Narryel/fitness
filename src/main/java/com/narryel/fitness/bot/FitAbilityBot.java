package com.narryel.fitness.bot;

import com.narryel.fitness.bot.handlers.command.CommandHandlerFactory;
import com.narryel.fitness.bot.handlers.input.UserInputHandlerFactory;
import com.narryel.fitness.configuration.properties.AbilityBotCredentials;
import com.narryel.fitness.domain.entity.UserState;
import com.narryel.fitness.domain.enums.State;
import com.narryel.fitness.repository.UserStateRepository;
import com.narryel.fitness.util.MessageGenerators;
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
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

@Slf4j
@Component
public class FitAbilityBot extends AbilityBot {

    private UserStateRepository stateRepository;
    private AbilityBotCredentials credentials;
    private UserInputHandlerFactory userInputHandlerFactory;
    private CommandHandlerFactory commandHandlerFactory;


    private static final String TOKEN = "945650861:AAEvgilL4B3ErwOcn2bGMiKVdFZqZ-58nls";
    private static final String USER_NAME = "BigBicepsBot";

    @Autowired
    public FitAbilityBot(AbilityBotCredentials credentials, UserStateRepository stateRepository, UserInputHandlerFactory userInputHandlerFactory, CommandHandlerFactory commandHandlerFactory) {
        this(TOKEN, USER_NAME);
        this.credentials = credentials;
        this.stateRepository = stateRepository;
        this.userInputHandlerFactory = userInputHandlerFactory;
        this.commandHandlerFactory = commandHandlerFactory;
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
                            button1.setCallbackData(REGISTER_USER_CMD.getValue());
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
                .name(REGISTER_USER_CMD.getValue())
                .info("register user")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> {
                    silent.send("Введите ваш никнейм", ctx.chatId());
                    stateRepository.save(new UserState(State.WAITING_FOR_USER_NICKNAME, ctx.chatId()));
                })
                .reply(ctx -> {
                    final var chatId = ctx.getCallbackQuery().getMessage().getChatId();
                    silent.send("Введите ваш никнейм", chatId);
                    stateRepository.save(new UserState(State.WAITING_FOR_USER_NICKNAME, chatId));
                }, callbackDataEquals(REGISTER_USER_CMD.getValue()))
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
                        stateRepository.delete(optional.get());
                    } else {
                        if (ctx.update().hasMessage()) {
                            log.error("can't parse user request. Request text: {}", ctx.update().getMessage().getText());
                        }
                        if (ctx.update().hasCallbackQuery()) {
                            log.error("can't parse user request. Request text: {}", ctx.update().getCallbackQuery().getData());
                        }
                        silent.send("не понимать", ctx.chatId());

                    }
                })
                .build();
    }


    private Predicate<Update> callbackDataEquals(String command) {
        return update -> {
            CallbackQuery callbackData = update.getCallbackQuery();
            if (callbackData == null) {
                return false;
            }
            return command.equals(callbackData.getData());
        };
    }

    public Reply planTrainingReply() {
        Consumer<Update> action = upd -> {
            final var chatId = upd.getCallbackQuery().getMessage().getChatId();
            silent.send("Введите название первого упражнение", chatId);
            stateRepository.save(new UserState(State.WAITING_FOR_EXERCISE_NAME, chatId));
        };

        return Reply.of(action, callbackDataEquals(PLAN_TRAINING_CMD.getValue()));
    }

    public Reply addExerciseReply() {
        Consumer<Update> action = upd -> {
            final var chatId = upd.getCallbackQuery().getMessage().getChatId();
            silent.send("Введите название упражнения", chatId);
            stateRepository.save(new UserState(State.WAITING_FOR_EXERCISE_NAME, chatId));
        };

        return Reply.of(action, callbackDataEquals(ADD_EXERCISE_CMD.getValue()));
    }

    public Reply getMenu() {
        Consumer<Update> action = upd -> {
            final var chatId = upd.getCallbackQuery().getMessage().getChatId();
            silent.execute(MessageGenerators.getMenu(chatId));
        };

        return Reply.of(action, callbackDataEquals(GET_MENU_CMD.getValue()));
    }

    public Reply startTraining() {
        Consumer<Update> action = upd -> {
            final var chatId = upd.getCallbackQuery().getMessage().getChatId();
            final var sendMessage = commandHandlerFactory.getHandler(START_TRAINING_CMD).handleCommand();
            sendMessage.setChatId(chatId);
            silent.execute(sendMessage);
        };

        return Reply.of(action, callbackDataEquals(START_TRAINING_CMD.getValue()));
    }


}
