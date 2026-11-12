package com.author.crm.dao;

import com.author.crm.dao.impl.TrainerDaoImpl;
import com.author.crm.model.Trainer;
import com.author.crm.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;

class TrainerDaoTest {
    private TrainerDaoImpl dao;
    private Map<Long, Trainer> storage;

    @BeforeEach
    void setUp() {
        dao = new TrainerDaoImpl();
        storage = spy(new HashMap<>());

        dao.setStorage(storage);
    }

    @Test
    void createsTrainerWithGeneratedIdAndFindsIt() {
        Trainer trainer = trainer(null, "John");

        assertSame(trainer, dao.create(trainer));
        assertEquals(1L, trainer.getId());
        assertSame(trainer, dao.findById(1L).orElseThrow());
        assertEquals(List.of(trainer), dao.findAll());
    }

    @Test
    void continuesIdsAfterExistingStorageEntries() {
        storage = spy(new HashMap<>());
        storage.put(4L, trainer(4L, "Existing"));
        dao.setStorage(storage);

        Trainer created = dao.create(trainer(null, "New"));

        assertEquals(5L, created.getId());
    }

    @Test
    void updatesExistingTrainer() {
        dao.create(trainer(2L, "Original"));
        Trainer replacement = trainer(2L, "Updated");

        assertTrue(dao.update(replacement));
        assertSame(replacement, dao.findById(2L).orElseThrow());
    }

    @Test
    void rejectsUpdateForMissingTrainer() {
        assertFalse(dao.update(trainer(null, "Missing")));
    }

    @Test
    void findAllReturnsCopyOfStorageValues() {
        Trainer trainer = dao.create(trainer(null, "John"));

        List<Trainer> result = dao.findAll();
        result.clear();

        assertSame(trainer, dao.findById(trainer.getId()).orElseThrow());
    }

    @Test
    void returnsEmptyResultsBeforeStorageIsInitialized() {
        TrainerDaoImpl uninitializedDao = new TrainerDaoImpl();

        assertThrows(IllegalStateException.class, () -> uninitializedDao.findById(1L));
        assertThrows(IllegalStateException.class, uninitializedDao::findAll);
        assertThrows(IllegalStateException.class, () -> uninitializedDao.create(trainer(null, "John")));
        assertThrows(IllegalStateException.class, () -> uninitializedDao.update(trainer(1L, "John")));
    }

    private Trainer trainer(Long id, String firstName) {
        return new Trainer(id, firstName, "Doe", firstName + ".Doe", "password", true, new TrainingType("Yoga"));
    }
}
