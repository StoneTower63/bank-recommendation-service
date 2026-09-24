# Bank Recommendation Service

Микросервис рекомендаций банковских продуктов для банка «Стар».

Сервис рекомендует клиентам новые банковские продукты на основе их финансового поведения.

## Стек технологий

- Java 17
- Spring Boot 4.1.1
- Spring Web (REST API)
- Spring Data JPA / Hibernate — для работы с правилами рекомендаций
- JDBC (JdbcTemplate) — для чтения данных клиентов (H2, read-only)
- PostgreSQL 15 — база данных для динамических правил (read/write)
- H2 Database — встроенная БД для данных клиентов и транзакций (read-only)
- Liquibase — миграции схемы БД
- Caffeine — кеширование SQL-запросов
- Jackson — работа с JSON
- Maven
- JUnit 5, Mockito

## Требования для запуска

- **Java 17+**
- **PostgreSQL 15** — установлен и запущен локально
    - База данных: `BRSbase`
    - Пользователь: `bankClient`, пароль: `clientPass`
- **Файл `transaction.mv.db`** — в корне проекта (H2)

## Запуск

1. Клонировать репозиторий:
   git clone https://github.com/StoneTower63/bank-recommendation-service.git
2. Открыть проект в IntelliJ IDEA.

3. Убедиться, что PostgreSQL запущен и база `BRSbase` создана.

4. Запустить главный класс `BankRecommendationServiceApplication`.

5. Сервис доступен на `http://localhost:8080`.

6. Liquibase автоматически создаст таблицу `rules` в PostgreSQL при старте.

## REST API

- `GET /recommendation/{user_id}` — получить рекомендации для клиента
- `POST /rule` — создать динамическое правило
- `GET /rule` — получить список всех правил
- `DELETE /rule/{id}` — удалить правило

Подробное описание — в [Wiki](https://github.com/StoneTower63/bank-recommendation-service/wiki).

## Документация

Полная документация — в [Wiki](https://github.com/StoneTower63/bank-recommendation-service/wiki):

- [Описание проекта](https://github.com/StoneTower63/bank-recommendation-service/wiki/Описание-проекта)
- [Требования (FR + NFR)](https://github.com/StoneTower63/bank-recommendation-service/wiki/Требования)
- [REST API](https://github.com/StoneTower63/bank-recommendation-service/wiki/REST-API)
- [Трейсинг требований](https://github.com/StoneTower63/bank-recommendation-service/wiki/Трейсинг-требований)

## Команда

- [StoneTower63](https://github.com/StoneTower63) — тимлид, разработчик, тестировщик
- [arina-baglaeva](https://github.com/arina-baglaeva) — разработчик, тестировщик
- [Annie-Falcon](https://github.com/Annie-Falcon) — разработчик, тестировщик

## Статус

Учебный проект Skypro. Спринт 1 и Спринт 2 завершены.