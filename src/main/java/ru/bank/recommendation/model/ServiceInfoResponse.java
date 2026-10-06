package ru.bank.recommendation.model;

/**
 * DTO с информацией о сборке сервиса.
 *
 * @param name    название сервиса
 * @param version версия сборки
 */
public record ServiceInfoResponse(String name, String version) {
}