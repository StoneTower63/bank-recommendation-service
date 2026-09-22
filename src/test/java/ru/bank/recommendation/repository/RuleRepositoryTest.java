package ru.bank.recommendation.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;


import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import ru.bank.recommendation.model.RuleEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RuleRepositoryTest {

    @Autowired
    private RuleRepository ruleRepository;

    private RuleEntity createTestEntity(String productName) {
        RuleEntity entity = new RuleEntity();
        entity.setProductName(productName);
        entity.setProductId(UUID.randomUUID());
        entity.setProductText("Тестовое описание");
        entity.setRule("[]");
        return entity;
    }

    @Test
    public void save_shouldAssignIdToEntity() {
        RuleEntity entity = createTestEntity("Тестовая карта 1");
        RuleEntity savedEntity = ruleRepository.save(entity);
        Assertions.assertNotNull(savedEntity.getId(), "ID должен быть присвоен после сохранения");
    }

    @Test
    public void findById_shouldReturnSavedEntity() {
        RuleEntity entity = createTestEntity("Тестовая карта 2");
        RuleEntity savedEntity = ruleRepository.save(entity);

        Optional<RuleEntity> foundEntity = ruleRepository.findById(savedEntity.getId());

        Assertions.assertTrue(foundEntity.isPresent(), "Сущность должна быть найдена");
        Assertions.assertEquals("Тестовая карта 2", foundEntity.get().getProductName());
    }

    @Test
    public void findAll_shouldReturnAtLeastTwoEntities() {
        RuleEntity entity1 = createTestEntity("Тестовая карта 1");
        RuleEntity entity2 = createTestEntity("Тестовая карта 2");

        ruleRepository.save(entity1);
        ruleRepository.save(entity2);

        List<RuleEntity> list = ruleRepository.findAll();

        Assertions.assertTrue(list.size() >= 2, "Должно быть хотя бы 2 записи");
    }

    @Test
    public void deleteById_shouldRemoveEntity() {
        RuleEntity entity = createTestEntity("Тестовая карта для удаления");
        RuleEntity savedEntity = ruleRepository.save(entity);
        Long id = savedEntity.getId();

        ruleRepository.deleteById(id);

        Optional<RuleEntity> foundEntity = ruleRepository.findById(id);
        Assertions.assertTrue(foundEntity.isEmpty(), "Сущность должна быть удалена");
    }
}