package ru.bank.recommendation.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.bank.recommendation.model.RecommendationDto;
import ru.bank.recommendation.model.RecommendationResponse;
import ru.bank.recommendation.model.UserDto;
import ru.bank.recommendation.repository.RecommendationRepository;
import ru.bank.recommendation.service.RecommendationService;

import java.util.List;

@Component
public class RecommendationBot implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;

    private final RecommendationRepository recommendationRepository;

    private final RecommendationService recommendationService;

    private static final String GREETING = "\uD83E\uDD16 *Привет\\! Я бот банка «Стар»\\.*";

    private static final String HELP_TEXT = "\n\n"
            + "_Я помогу подобрать для вас новые банковские продукты\\._\n"
            + "\n"
            + "Команда\\:\n"
            + "`\\/recommend \\<Имя Фамилия\\>` — получить рекомендации\\.\n"
            + "Например\\: `\\/recommend Иван Иванов`";

    public RecommendationBot(TelegramClient telegramClient, RecommendationRepository recommendationRepository, RecommendationService recommendationService) {
        this.telegramClient = telegramClient;
        this.recommendationRepository = recommendationRepository;
        this.recommendationService = recommendationService;
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
        } else if (messageText.startsWith("/recommend")) {
            handleRecommendCommand(chatId, messageText);
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

    private void handleRecommendCommand(long chatId, String commandText) {
        UserDto user = findUserByCommand(commandText);

        if (user == null) {
            sendMessage(chatId, "Пользователь не найден\\!");
            return;
        }
        // Получаем рекомендации от сервиса
        RecommendationResponse response = recommendationService.getUserRecommendations(user.id());
        // Формируем текст сообщения
        String messageText = buildAnswer(user, response.getRecommendations());

        sendMessage(chatId, messageText);
    }

    /* Выделяет ФИО из строки команды и ищет пользователя в БД.
       Возвращает null, если пользователь не найден или формат ввода неверный. */
    private UserDto findUserByCommand(String commandText) {
        // ищем первый пробел после команды "/recommend"
        int spaceIndex = commandText.indexOf(' ');
        if (spaceIndex == -1 || spaceIndex == commandText.length() - 1) {
            return null;
        }
        // отделяем команду "/recommend" от <Имя Фамилия>
        String fullName = commandText.substring(spaceIndex + 1).trim();
        spaceIndex = fullName.indexOf(' '); // Ищем второй пробел в <Имя Фамилия>

        if (spaceIndex == -1) {
            return null; // Введено только одно слово вместо ФИО
        }

        String firstName = fullName.substring(0, spaceIndex);
        String lastName = fullName.substring(spaceIndex + 1);

        // Поиск пользователей
        List<UserDto> users = recommendationRepository.findUsersByName(firstName, lastName);

        // Обработка сценариев количества пользователей
        if (users.size() != 1) {
            return null;
        }
        return users.get(0);
    }

    // Собирает итоговую строку ответа согласно критериям приемки.
    private String buildAnswer(UserDto user, List<RecommendationDto> recommendations) {
        StringBuilder answer = new StringBuilder();

        // Экранируем спецсимволы Markdown в именах
        String safeFirstName = escapeMarkdown(user.firstName());
        String safeLastName = escapeMarkdown(user.lastName());

        answer.append("Здравствуйте ").append(safeFirstName).append(" ").append(safeLastName).append("\n");
        answer.append("Новые продукты для вас\\:\n");

        if (recommendations == null || recommendations.isEmpty()) {
            answer.append("Пока нет новых предложений");
        } else {
            // Форматирование списка продуктов
            for (RecommendationDto dto : recommendations) {
                String safeName = escapeMarkdown(dto.getName());
                answer.append("• ").append(safeName).append("\n");
            }
        }
        return answer.toString().trim();
    }

    // Вспомогательный метод для экранирования спецсимволов
    private String escapeMarkdown(String text) {
        if (text == null) return "";
        return text.replaceAll("([*_\\\\`])", "\\\\$1");
    }
}