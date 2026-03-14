package org.dach.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

@ApplicationScoped
public class BotMessageService {

    private static final Logger logger = LoggerFactory.getLogger(BotMessageService.class);

    private static final String HELP_TEXT = """
            <b>Every Soft Ping Bot</b> — bot for quickly notifying chat participants.

            <b>How to use:</b>
            <code>@%s ping</code> — mention all chat participants
            <code>@%s help</code> — display this message
            <code>@%s about</code> — information about the creator

            The bot automatically remembers participants who post in the chat.""";

    private static final String ABOUT_TEXT = """
            <b>Every Soft Ping Bot</b>

            Developer: @dmitrychis
            GitHub: <a href="https://github.com/oURMIo/every-soft-ping-bot">link</a>
            Business Card: <a href="https://dmitrych.ddns.net/">link</a>""";

    private final ChatMemberService chatMemberService;
    private final PingService pingService;

    public BotMessageService(ChatMemberService chatMemberService, PingService pingService) {
        this.chatMemberService = chatMemberService;
        this.pingService = pingService;
    }

    public void registerListener(TelegramBot bot) {
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                try {
                    processUpdate(bot, update);
                } catch (Exception e) {
                    logger.error("Error processing update {}: {}", update.updateId(), e.getMessage(), e);
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> logger.error("Updates listener error: {}", e.getMessage(), e));
    }

    @ActivateRequestContext
    void processUpdate(TelegramBot bot, Update update) {
        Message message = update.message();
        if (message == null) {
            return;
        }

        long chatId = message.chat().id();

        if (message.from() != null) {
            chatMemberService.registerMember(chatId, message.from());
        }

        if (message.newChatMembers() != null) {
            for (User user : message.newChatMembers()) {
                chatMemberService.registerMember(chatId, user);
            }
        }

        if (message.text() != null) {
            String command = pingService.extractBotCommand(message);
            if (command != null) {
                handleCommand(bot, message, command);
            }
        }
    }

    private void handleCommand(TelegramBot bot, Message message, String command) {
        long chatId = message.chat().id();

        switch (command) {
            case "ping" -> pingService.pingAll(bot, message);
            case "help" -> {
                String botName = pingService.getBotUsername();
                String text = String.format(HELP_TEXT, botName, botName, botName);
                bot.execute(new SendMessage(chatId, text).parseMode(ParseMode.HTML));
            }
            case "about" -> bot.execute(new SendMessage(chatId, ABOUT_TEXT).parseMode(ParseMode.HTML));
            default -> logger.debug("Unknown command '{}' in chat '{}'", command, chatId);
        }
    }
}