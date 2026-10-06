package ru.bank.recommendation.bot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.bank.recommendation.model.RecommendationDto;
import ru.bank.recommendation.model.RecommendationResponse;
import ru.bank.recommendation.model.UserDto;
import ru.bank.recommendation.repository.RecommendationRepository;
import ru.bank.recommendation.service.RecommendationService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationBotTest {

    private static final long TEST_CHAT_ID = 123456789L;
    private static final UUID TEST_USER_ID = UUID.randomUUID();
    private static final String TEST_FIRST_NAME = "Иван";
    private static final String TEST_LAST_NAME = "Иванов";
    private static final String FULL_NAME = TEST_FIRST_NAME + " " + TEST_LAST_NAME;
    private static final String COMMAND_TEXT = "/recommend " + FULL_NAME;
    long testChatId = 123456789L;
    @Mock
    private TelegramClient telegramClient;
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private RecommendationService recommendationService;
    @InjectMocks
    private RecommendationBot recommendationBot;

    private UserDto createTestUser() {
        return new UserDto(TEST_USER_ID, TEST_FIRST_NAME, TEST_LAST_NAME);
    }

    private RecommendationDto createTestRecommendation(UUID idRecommendationDto, String name, String text) {
        return new RecommendationDto(idRecommendationDto, name, text);
    }

    private Message createMockMessage(String text, long chatId) {
        Message message = mock(Message.class);
        when(message.getText()).thenReturn(text);
        when(message.getChatId()).thenReturn(chatId);
        return message;
    }

    private Update createMockUpdate(Message message) {
        Update update = mock(Update.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(update.getMessage().hasText()).thenReturn(true);
        return update;
    }

    @Test
    @DisplayName("Должен отправить приветствие и помощь при получении команды /start")
    void consume_shouldSendGreetingAndHelp_whenStartCommandReceived() throws TelegramApiException {
        Chat chat = Chat.builder()
                .id(testChatId)
                .type("private")
                .build();

        User from = User.builder()
                .id(1L)
                .isBot(false)
                .firstName("Test")
                .build();

        Message message = Message.builder()
                .messageId(1)
                .date((int) (System.currentTimeMillis() / 1000))
                .chat(chat)
                .from(from)
                .text("/start")
                .build();

        Update update = new Update();
        update.setMessage(message);

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);

        recommendationBot.consume(update);

        verify(telegramClient, times(1)).execute(messageCaptor.capture());

        SendMessage capturedMessage = messageCaptor.getValue();

        assertEquals(String.valueOf(testChatId), capturedMessage.getChatId(), "Chat ID должен совпадать с ID из сообщения");

        String actualText = capturedMessage.getText();
        assertTrue(actualText.contains("Привет"), "Текст должен содержать приветствие");
        assertTrue(actualText.contains("банка «Стар»"), "Текст должен содержать название банка");
        assertTrue(actualText.contains("/recommend"), "Текст должен содержать команду /recommend");
        assertTrue(actualText.contains("Иван Иванов"), "Текст должен содержать пример команды");

        assertEquals(ParseMode.MARKDOWNV2, capturedMessage.getParseMode(), "Режим парсинга должен быть MARKDOWNV2");

        verifyNoInteractions(recommendationRepository, recommendationService);
    }

    @Test
    @DisplayName("Должен игнорировать сообщение, если оно не текстовое")
    void consume_shouldIgnore_whenMessageHasNoText() {
        Chat chat = Chat.builder()
                .id(testChatId)
                .type("private")
                .build();

        User from = User.builder()
                .id(1L)
                .isBot(false)
                .firstName("Test")
                .build();

        Message message = Message.builder()
                .messageId(1)
                .date((int) (System.currentTimeMillis() / 1000))
                .chat(chat)
                .from(from)
                .text(null)
                .build();

        Update update = new Update();
        update.setMessage(message);

        recommendationBot.consume(update);

        verifyNoInteractions(telegramClient, recommendationRepository, recommendationService);
    }

    @Test
    @DisplayName("Должен игнорировать Update, если в нем нет сообщения")
    void consume_shouldIgnore_whenUpdateHasNoMessage() {
        Update update = new Update();

        recommendationBot.consume(update);

        verifyNoInteractions(telegramClient, recommendationRepository, recommendationService);
    }

    @Test
    @DisplayName("1 пользователь + рекомендации: должен отправить список продуктов")
    void handleRecommend_oneUser_withRecommendations() throws TelegramApiException {
        UserDto user = createTestUser();
        RecommendationDto rec1 = createTestRecommendation(UUID.randomUUID(), "Кредитная карта", "");
        RecommendationDto rec2 = createTestRecommendation(UUID.randomUUID(), "Вклад", "");

        when(recommendationRepository.findUsersByName(TEST_FIRST_NAME, TEST_LAST_NAME))
                .thenReturn(List.of(user));

        when(recommendationService.getUserRecommendations(TEST_USER_ID))
                .thenReturn(new RecommendationResponse(user.id(), List.of(rec1, rec2)));

        Message message = createMockMessage(COMMAND_TEXT, TEST_CHAT_ID);
        Update update = createMockUpdate(message);

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);

        recommendationBot.consume(update);

        verify(recommendationRepository, times(1))
                .findUsersByName(TEST_FIRST_NAME, TEST_LAST_NAME);
        verify(recommendationService, times(1))
                .getUserRecommendations(TEST_USER_ID);

        verify(telegramClient, times(1)).execute(messageCaptor.capture());
        String text = messageCaptor.getValue().getText();

        assertTrue(text.contains("Здравствуйте Иван Иванов"));
        assertTrue(text.contains("Кредитная карта"));
        assertTrue(text.contains("Вклад"));
        verify(recommendationService, times(1)).getUserRecommendations(TEST_USER_ID);
    }

    @Test
    @DisplayName("1 пользователь + 0 рекомендаций: должен отправить заглушку")
    void handleRecommend_oneUser_noRecommendations() throws TelegramApiException {
        UserDto user = createTestUser();
        when(recommendationRepository.findUsersByName(TEST_FIRST_NAME, TEST_LAST_NAME))
                .thenReturn(List.of(user));

        when(recommendationService.getUserRecommendations(TEST_USER_ID))
                .thenReturn(new RecommendationResponse(user.id(), Collections.emptyList()));

        Message message = createMockMessage(COMMAND_TEXT, TEST_CHAT_ID);
        Update update = createMockUpdate(message);

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);

        recommendationBot.consume(update);

        verify(telegramClient, times(1)).execute(messageCaptor.capture());
        String text = messageCaptor.getValue().getText();

        assertTrue(text.contains("Здравствуйте Иван Иванов"));
        assertTrue(text.contains("Пока нет новых предложений"));
    }

    @Test
    @DisplayName("0 пользователей: должен сообщить, что пользователь не найден")
    void handleRecommend_noUsers() throws TelegramApiException {
        when(recommendationRepository.findUsersByName(TEST_FIRST_NAME, TEST_LAST_NAME))
                .thenReturn(Collections.emptyList());

        Message message = createMockMessage(COMMAND_TEXT, TEST_CHAT_ID);
        Update update = createMockUpdate(message);

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);

        recommendationBot.consume(update);

        verify(telegramClient, times(1)).execute(messageCaptor.capture());
        String text = messageCaptor.getValue().getText();

        assertEquals("Пользователь не найден\\!", text);
        verify(recommendationService, never()).getUserRecommendations(UUID.randomUUID());
    }

    @Test
    @DisplayName("2 пользователя: должен сообщить, что пользователь не найден")
    void handleRecommend_multipleUsers() throws TelegramApiException {
        UserDto user1 = createTestUser();
        UserDto user2 = new UserDto(UUID.randomUUID(), TEST_FIRST_NAME, TEST_LAST_NAME);

        when(recommendationRepository.findUsersByName(TEST_FIRST_NAME, TEST_LAST_NAME))
                .thenReturn(List.of(user1, user2));

        Message message = createMockMessage(COMMAND_TEXT, TEST_CHAT_ID);
        Update update = createMockUpdate(message);

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);

        recommendationBot.consume(update);

        verify(telegramClient, times(1)).execute(messageCaptor.capture());
        String text = messageCaptor.getValue().getText();

        assertEquals("Пользователь не найден\\!", text);
        verify(recommendationService, never()).getUserRecommendations(UUID.randomUUID());
    }
}