package com.author.crm.dao;

import com.author.crm.dao.impl.TrainingDaoImpl;
import com.author.crm.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;

class TrainingDaoTest {
    private TrainingDaoImpl dao;
    private Map<Long, Training> storage;

    @BeforeEach
    void setUp() {
        dao = new TrainingDaoImpl();
        storage = spy(new HashMap<>());

        dao.setStorage(storage);
    }

    @Test
    void createsTrainingWithGeneratedIdAndFindsIt() {
        Training training = training(null, "Cardio");

        assertSame(training, dao.create(training));
        assertEquals(1L, training.getId());
        assertSame(training, dao.findById(1L).orElseThrow());
        assertEquals(List.of(training), dao.findAll());
    }

    @Test
    void continuesIdsAfterExistingStorageEntries() {
        storage = spy(new HashMap<>());
        storage.put(6L, training(6L, "Existing"));
        dao.setStorage(storage);

        Training created = dao.create(training(null, "New"));

        assertEquals(7L, created.getId());
    }

    @Test
    void findAllReturnsCopyOfStorageValues() {
        Training training = dao.create(training(null, "Cardio"));

        List<Training> result = dao.findAll();
        result.clear();

        assertSame(training, dao.findById(training.getId()).orElseThrow());
    }

    @Test
    void returnsEmptyResultsBeforeStorageIsInitialized() {
        TrainingDaoImpl uninitializedDao = new TrainingDaoImpl();

        assertTrue(uninitializedDao.findById(1L).isEmpty());
        assertTrue(uninitializedDao.findAll().isEmpty());
        assertThrows(IllegalStateException.class, () -> uninitializedDao.create(training(null, "Cardio")));
    }

    private Training training(Long id, String name) {
        return new Training(id, 1L, 2L, name, "Fitness", LocalDate.of(2026, 1, 1), 45);
    }
}
