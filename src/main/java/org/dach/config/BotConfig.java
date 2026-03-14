package org.dach.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pengrad.telegrambot.TelegramBot;

@ApplicationScoped
public class BotConfig {

    private static final Logger logger = LoggerFactory.getLogger(BotConfig.class);

    @ConfigProperty(name = "bot.token")
    String botToken;

    @Produces
    @ApplicationScoped
    public TelegramBot telegramBot() {
        logger.info("Creating Telegram bot bean");
        return new TelegramBot(botToken);
    }
}