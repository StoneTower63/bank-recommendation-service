package ru.bank.recommendation.model;

import java.util.UUID;

public record UserDto(UUID id, String firstName, String lastName) {}

