package com.author.crm.dao;

import com.author.crm.dao.impl.TraineeDaoImpl;
import com.author.crm.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class TraineeDaoTest {
    private TraineeDaoImpl dao;
    private Map<Long, Trainee> storage;

    @BeforeEach
    void setUp() {
        dao = new TraineeDaoImpl();
        storage = spy(new HashMap<>());

        dao.setStorage(storage);
    }

    @Test
    void createsTraineeWithGeneratedIdAndFindsIt() {
        Trainee trainee = trainee(null, "John");

        assertSame(trainee, dao.create(trainee));
        assertEquals(1L, trainee.getId());
        assertSame(trainee, dao.findById(1L).orElseThrow());
        assertEquals(List.of(trainee), dao.findAll());
    }

    @Test
    void continuesIdsAfterExistingStorageEntries() {
        storage = spy(new HashMap<>());
        storage.put(7L, trainee(7L, "Existing"));
        dao.setStorage(storage);

        Trainee created = dao.create(trainee(null, "New"));

        assertEquals(8L, created.getId());
    }

    @Test
    void preservesExplicitIdAndUpdatesExistingTrainee() {
        Trainee original = trainee(3L, "Original");
        dao.create(original);
        Trainee replacement = trainee(3L, "Updated");

        assertTrue(dao.update(replacement));
        assertSame(replacement, dao.findById(3L).orElseThrow());
    }

    @Test
    void rejectsUpdateForMissingTrainee() {
        assertFalse(dao.update(trainee(99L, "Missing")));
    }

    @Test
    void deletesTraineeAndReturnsFalseForUnknownId() {
        Trainee trainee = dao.create(trainee(null, "John"));

        assertTrue(dao.deleteById(trainee.getId()));

        verify(storage).remove(trainee.getId());
        assertTrue(dao.findById(trainee.getId()).isEmpty());
        assertFalse(dao.deleteById(999L));
        assertTrue(dao.findById(999L).isEmpty());
        assertTrue(dao.findAll().isEmpty());
    }

    @Test
    void findAllReturnsCopyOfStorageValues() {
        Trainee trainee = dao.create(trainee(null, "John"));

        List<Trainee> result = dao.findAll();
        result.clear();

        assertSame(trainee, dao.findById(trainee.getId()).orElseThrow());
    }

    @Test
    void rejectsOperationsThatRequireUninitializedStorage() {
        TraineeDaoImpl uninitializedDao = new TraineeDaoImpl();

        assertThrows(IllegalStateException.class, () -> uninitializedDao.create(trainee(null, "John")));
        assertThrows(IllegalStateException.class, () -> uninitializedDao.update(trainee(1L, "John")));
        assertThrows(IllegalStateException.class, () -> uninitializedDao.findById(1L));
        assertThrows(IllegalStateException.class, uninitializedDao::findAll);
    }

    private Trainee trainee(Long id, String firstName) {
        return new Trainee(id, firstName, "Smith", firstName + ".Smith", "password", true,
                LocalDate.of(1990, 1, 1), "Address");
    }
}
