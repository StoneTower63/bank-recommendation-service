package ru.bank.recommendation.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.bank.recommendation.bot.RecommendationBot;

@Configuration
public class TelegramBotConfiguration {

    @Bean
    public TelegramClient telegramClient(@Value("${telegram.bot.token}") String token) {
        return new OkHttpTelegramClient(token);
    }

    @Bean
    public TelegramBotsLongPollingApplication telegramBotsApplication() {
        return new TelegramBotsLongPollingApplication();
    }

    @Bean
    public RecommendationBot recommendationBot(TelegramClient telegramClient,
                                               TelegramBotsLongPollingApplication application,
                                               @Value("${telegram.bot.username}") String botUsername,
                                               @Value("${telegram.bot.token}") String botToken) {
        RecommendationBot bot = new RecommendationBot(telegramClient);

        try {
            application.registerBot(botToken, bot);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось зарегистрировать бота", e);
        }

        return bot;
    }
}