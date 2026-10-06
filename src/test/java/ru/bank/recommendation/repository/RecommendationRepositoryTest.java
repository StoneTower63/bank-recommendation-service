package ru.bank.recommendation.repository;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.bank.recommendation.model.UserDto;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

public class RecommendationRepositoryTest {
    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private RecommendationRepository repository;

    @Test
    void shouldFindUserWhenExists() {
        MockitoAnnotations.openMocks(this);

        var userId = UUID.randomUUID();
        var expectedUser = new UserDto(userId, "John", "Doe");

        given(jdbcTemplate.query(any(), any(RowMapper.class), any(), any())).willReturn(List.of(expectedUser));

        var result = repository.findUsersByName("john", "doe");

        assertThat(result).isNotEmpty()
                .hasSize(1)
                .containsExactly(expectedUser);
    }

    @Test
    void shouldReturnEmptyListIfNoMatch() {
        MockitoAnnotations.openMocks(this);

        given(jdbcTemplate.query(any(), any(RowMapper.class), any(), any())).willReturn(Collections.emptyList());

        var result = repository.findUsersByName("nonexistent", "user");

        assertThat(result).isEmpty();
    }
}
