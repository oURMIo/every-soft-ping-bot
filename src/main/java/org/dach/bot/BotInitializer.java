package org.dach.bot;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import org.dach.service.BotMessageService;
import org.dach.service.PingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pengrad.telegrambot.TelegramBot;

@ApplicationScoped
public class BotInitializer {

    private static final Logger logger = LoggerFactory.getLogger(BotInitializer.class);

    private final TelegramBot bot;
    private final BotMessageService botMessageService;
    private final PingService pingService;

    public BotInitializer(TelegramBot bot, BotMessageService botMessageService, PingService pingService) {
        this.bot = bot;
        this.botMessageService = botMessageService;
        this.pingService = pingService;
    }

    void onStart(@Observes StartupEvent event) {
        logger.info("Starting Telegram ping bot...");
        pingService.initBotUsername(bot);
        botMessageService.registerListener(bot);
    }
}