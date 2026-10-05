# Bank Recommendation Service

Микросервис рекомендаций банковских продуктов для банка «Стар».

Сервис рекомендует клиентам новые банковские продукты на основе их финансового поведения: транзакций, типов продуктов,
сумм пополнений и трат.

## Стек технологий

- Java 17 (запуск на OpenJDK 24)
- Spring Boot 4.1.1
- Spring Web (REST API)
- Spring Data JPA / Hibernate — динамические правила рекомендаций
- Spring Data JDBC + JdbcTemplate — чтение данных клиентов (H2, read-only)
- PostgreSQL 15 — read/write база для динамических правил
- H2 Database 2.4.240 — read-only база с данными клиентов
- Liquibase 5.0.3 — миграции схемы PostgreSQL
- Caffeine 3.2.4 — кеширование SQL-запросов к H2
- Jackson — работа с JSON
- Telegram Bot API (`telegrambots-longpolling` 7.10.0 + `telegrambots-client` 7.10.0)
- Maven
- JUnit 5, Mockito 5.23.0

## Требования для запуска

- **Java 17+**
- **PostgreSQL 15** — установлен и запущен локально
    - База данных: `BRSbase`
    - Пользователь: `bankClient`, пароль: `clientPass`
- **Файл `transaction.mv.db`** — в корне проекта (H2, ~13 МБ)
- **VPN** — обязателен для работы Telegram Bot API (`api.telegram.org` заблокирован в РФ)
- **Свой Telegram-бот** — получить токен у [@BotFather](https://t.me/BotFather)

## Запуск

1. Клонировать репозиторий:
   git clone https://github.com/StoneTower63/bank-recommendation-service.git
2. Открыть проект в IntelliJ IDEA.

3. Убедиться, что PostgreSQL запущен и база `BRSbase` создана.

4. Создать `src/main/resources/application-local.properties` со своим токеном бота:
   ```properties
   telegram.bot.token=ВАШ_ТОКЕН
   ```

Файл **не коммитится** — он в `.gitignore`.

5. В IntelliJ: **Run -> Edit Configurations -> Active profiles = `local`**.

6. Запустить главный класс `BankRecommendationServiceApplication`.

7. Сервис доступен на `http://localhost:8080`.

8. Liquibase автоматически применит миграции к PostgreSQL при старте.

> **Без VPN** бот не зарегистрируется с ошибкой `TelegramApiErrorResponseException`. Это ожидаемо — включите VPN и
> перезапустите.
>
> **Без токена** приложение можно запустить, отключив бота: добавьте в Run Configuration `-Dtelegram.bot.enabled=false`
> (VM options).

## REST API

| Метод    | Путь                        | Описание                                              |
|----------|-----------------------------|-------------------------------------------------------|
| `GET`    | `/recommendation/{user_id}` | Рекомендации для клиента по UUID                      |
| `POST`   | `/rule`                     | Создать динамическое правило                          |
| `GET`    | `/rule`                     | Список всех правил                                    |
| `DELETE` | `/rule/{id}`                | Удалить правило (со статистикой)                      |
| `GET`    | `/rule/stats`               | Статистика срабатываний всех правил (включая count=0) |
| `POST`   | `/management/clear-caches`  | Очистить все Caffeine-кеши                            |
| `GET`    | `/management/info`          | Информация о сборке (version, name, build time)       |

Полная документация в формате OpenAPI — на странице Wiki «REST API».

## Telegram-бот

- **Имя**: Bank Star Recommendation Bot
- **Username**: [@bank_star_recommend_bot](https://t.me/bank_star_recommend_bot)
- **Токен**: в `application-local.properties` (не коммитится)

Команды:

| Команда                    | Действие                                     |
|----------------------------|----------------------------------------------|
| `/start`                   | Приветствие и краткая справка                |
| `/recommend <Имя Фамилия>` | Список рекомендованных продуктов для клиента |

Сценарии `/recommend`:

- **1 найден, есть рекомендации** -> список продуктов
- **1 найден, нет рекомендаций** -> «Пока нет новых предложений»
- **0 найдено** -> «Пользователь не найден!»
- **>1 найдено** -> «Пользователь не найден!» (нужно уточнить ФИО)

## Тестирование

Запуск всех тестов:

```
./mvnw test
```

Что покрыто:

- Unit-тесты сервисов и DTO (`QueryCheckerTest`, `RuleServiceTest`, `RuleStatsServiceTest`, `*DtoTest`)
- Тесты репозиториев (`RuleRepositoryTest`, `RuleStatsRepositoryTest`, `RecommendationRepositoryTest`)
- Controller-тесты (`RuleControllerTest`, `RuleControllerStatsTest`, `ManagementControllerClearCachesTest`,
  `ManagementControllerInfoTest`, `RecommendationControllerTest`)
- Интеграционные тесты (`RecommendationIntegrationTest`, `RuleDeletionIntegrationTest`)
- Тесты бота (`RecommendationBotTest`)

Всего 57 тестов.

> Тесты работают с **локальной PostgreSQL** (`BRSbase`) — убедитесь, что она запущена. Переключение на in-memory H2 не
> поддерживается из-за `JSONB` в миграциях Liquibase.

## Архитектура

Приложение состоит из трёх слоёв:

- **Контроллеры** — REST-эндпоинты (`RecommendationController`, `RuleController`, `ManagementController`).
- **Сервисы** — бизнес-логика (`RecommendationService`, `RuleService`, `RuleStatsService`, `QueryChecker`,
  `RecommendationBot`).
- **Репозитории** — работа с БД (`RecommendationRepository` через JdbcTemplate, `RuleRepository` и `RuleStatsRepository`
  через JPA).

Используются две базы данных: H2 (read-only, данные клиентов) и PostgreSQL (read/write, правила и статистика).

Компонентная диаграмма и диаграмма деятельности — на странице Wiki «Архитектура».

## Развёртывание

Инструкция по развёртыванию (требуемые сервисы, команды сборки и запуска, переменные среды) — на странице Wiki
«Развёртывание».

## Структура проекта

```
ru.bank.recommendation
├── bot/              — RecommendationBot (Telegram)
├── cache/            — CacheKey
├── configuration/    — конфиги DataSource (PostgreSQL @Primary, H2), Telegram
├── controller/       — RecommendationController, RuleController, ManagementController
├── enums/            — ComparisonOperator
├── model/            — DTO и JPA-сущности
├── repository/       — JPA-репозитории + JdbcTemplate-репозиторий
└── service/          — RecommendationService, RuleService, RuleStatsService, QueryChecker
```

## Документация

Полная документация — в [Wiki](https://github.com/StoneTower63/bank-recommendation-service/wiki):

- [Главная](https://github.com/StoneTower63/bank-recommendation-service/wiki)
- [Описание проекта](https://github.com/StoneTower63/bank-recommendation-service/wiki/Описание-проекта)
- [Требования (User Story + NFR)](https://github.com/StoneTower63/bank-recommendation-service/wiki/Требования)
- [REST API (OpenAPI)](https://github.com/StoneTower63/bank-recommendation-service/wiki/REST-API)
- [Архитектура](https://github.com/StoneTower63/bank-recommendation-service/wiki/Архитектура)
- [Развёртывание](https://github.com/StoneTower63/bank-recommendation-service/wiki/Развёртывание)
- [Трейсинг требований](https://github.com/StoneTower63/bank-recommendation-service/wiki/Трейсинг-требований)

## Команда

- [StoneTower63](https://github.com/StoneTower63) — тимлид, разработчик, тестировщик, техническая документация
- [arina-baglaeva](https://github.com/arina-baglaeva) — разработчик, тестировщик
- [Annie-Falcon](https://github.com/Annie-Falcon) — разработчик, тестировщик

## Статус

Учебный проект Skypro. Спринты 1, 2, 3 и 4 завершены.
