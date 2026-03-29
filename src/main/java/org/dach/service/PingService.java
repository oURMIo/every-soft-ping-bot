package org.dach.service;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.dach.domain.ChatMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyParameters;
import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetMeResponse;
import com.pengrad.telegrambot.response.SendResponse;

@ApplicationScoped
public class PingService {

    private static final Logger logger = LoggerFactory.getLogger(PingService.class);
    private static final long RATE_LIMIT_MS = 30_000;

    private final ChatMemberService chatMemberService;
    private final Map<Long, Long> lastPingTimestamp = new ConcurrentHashMap<>();
    public static final String EMPTY_USERS_MESSAGE = "I don't know who's in the chat yet. Post something so I can remember you!";
    public static final String UNKNOWN_CHAT_MEMBER_MESSAGE = "I don't know anyone else in this chat besides you.";
    private volatile String botUsername;

    public PingService(ChatMemberService chatMemberService) {
        this.chatMemberService = chatMemberService;
    }

    public String getBotUsername() {
        return botUsername;
    }

    public void initBotUsername(TelegramBot bot) {
        try {
            GetMeResponse response = bot.execute(new GetMe());
            if (response.isOk() && response.user() != null && response.user().username() != null) {
                botUsername = response.user().username().toLowerCase();
                logger.info("Bot username initialized successfully");
            } else {
                logger.error("Failed to get bot username: {}", response.description());
            }
        } catch (Exception e) {
            logger.error("Error initializing bot username: {}", e.getMessage(), e);
        }
    }

    public String extractBotCommand(Message message) {
        if (botUsername == null) {
            logger.warn("Bot username not initialized, ignoring command check");
            return null;
        }

        String text = message.text();
        MessageEntity[] entities = message.entities();
        if (entities == null) {
            return null;
        }

        for (MessageEntity entity : entities) {
            if (entity.type() == MessageEntity.Type.mention) {
                int end = entity.offset() + entity.length();
                if (end > text.length()) {
                    continue;
                }
                String mentionText = text.substring(entity.offset(), end);
                if (mentionText.equalsIgnoreCase("@" + botUsername)) {
                    String afterMention = text.substring(end).trim().toLowerCase();
                    if (afterMention.isEmpty()) {
                        return null;
                    }
                    int spaceIndex = afterMention.indexOf(' ');
                    return spaceIndex > 0 ? afterMention.substring(0, spaceIndex) : afterMention;
                }
            }
        }
        return null;
    }

    public void pingAll(TelegramBot bot, Message message) {
        long chatId = message.chat().id();
        long senderId = message.from().id();

        long now = System.currentTimeMillis();
        Long lastPing = lastPingTimestamp.get(chatId);
        if (lastPing != null && (now - lastPing) < RATE_LIMIT_MS) {
            long remainingSec = (RATE_LIMIT_MS - (now - lastPing)) / 1000;
            String msg = String.format("Wait %d seconds before the next ping", remainingSec);
            bot.execute(new SendMessage(chatId, msg));
            return;
        }
        lastPingTimestamp.put(chatId, now);

        List<ChatMember> members = chatMemberService.getMembers(chatId);
        logger.info("Chat '{}' has {} tracked members", chatId, members.size());

        if (members.isEmpty()) {
            bot.execute(new SendMessage(chatId, EMPTY_USERS_MESSAGE));
            return;
        }

        String mentions = members.stream()
            .filter(m -> m.getUserId() != senderId)
            .map(ChatMember::toMentionHtml)
            .collect(Collectors.joining(" "));

        if (mentions.isBlank()) {
            bot.execute(new SendMessage(chatId, UNKNOWN_CHAT_MEMBER_MESSAGE));
            return;
        }

        logger.info("Pinging all members in chat '{}'", chatId);
        SendMessage sendMessage = new SendMessage(chatId, mentions)
            .parseMode(ParseMode.HTML);
        sendMessage.replyParameters(new ReplyParameters(message.messageId()));
        SendResponse response = bot.execute(sendMessage);

        if (!response.isOk()) {
            logger.error("Failed to send ping: {} {}", response.errorCode(), response.description());
        }
    }
}