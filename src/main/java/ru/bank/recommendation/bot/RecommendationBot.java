package ru.bank.recommendation.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class RecommendationBot implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;

    private static final String GREETING = "\uD83E\uDD16 *Привет\\! Я бот банка «Стар»\\.*";

    private static final String HELP_TEXT = "\n\n"
            + "_Я помогу подобрать для вас новые банковские продукты\\._\n"
            + "\n"
            + "Команда\\:\n"
            + "`\\/recommend \\<Имя Фамилия\\>` — получить рекомендации\\.\n"
            + "Например\\: `\\/recommend Иван Иванов`";

    public RecommendationBot(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @Override
    public void consume(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        if (messageText.equals("/start")) {
            sendMessage(chatId, GREETING + HELP_TEXT);
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode(ParseMode.MARKDOWNV2)
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Ошибка отправки сообщения в Telegram", e);
        }
    }
}