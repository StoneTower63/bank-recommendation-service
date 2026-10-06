package ru.bank.recommendation.model;

import java.util.UUID;

/**
 * DTO с основными данными клиента банка.
 *
 * @param id        UUID пользователя
 * @param firstName имя
 * @param lastName  фамилия
 */
public record UserDto(UUID id, String firstName, String lastName) {
}

