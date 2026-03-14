package org.dach.config;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import io.quarkus.test.Mock;
import org.dach.bot.BotInitializer;
import org.dach.service.BotMessageService;
import org.dach.service.PingService;
import com.pengrad.telegrambot.TelegramBot;

@Mock
@ApplicationScoped
public class TestBotConfig extends BotInitializer {

    public TestBotConfig() {
        super(new TelegramBot("test-token"), null, null);
    }

    void onStart(@Observes StartupEvent event) {
        // do nothing during tests
    }
}