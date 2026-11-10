package com.author.crm.service;

import com.author.crm.dao.TraineeDao;
import com.author.crm.dao.TrainerDao;
import com.author.crm.model.Trainee;
import com.author.crm.model.Trainer;
import com.author.crm.model.TrainingType;
import com.author.crm.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {
    @Mock
    private TrainerDao trainerDao;
    @Mock
    private TraineeDao traineeDao;
    private TrainerServiceImpl trainerService;

    @BeforeEach
    void setUp() {
        trainerService = new TrainerServiceImpl();
        trainerService.setUsernameGenerator(new UsernameGenerator(traineeDao, trainerDao));
    }

    @Test
    void createsTrainerWithCredentialsAndDelegatesToDao() {
        mockDefaultCreation();
        Trainer trainer = trainer("John", "Doe");

        Trainer created = trainerService.create(trainer);

        assertSame(trainer, created);
        assertEquals("John.Doe", trainer.getUsername());
        assertEquals(10, trainer.getPassword().length());
        verify(trainerDao).create(trainer);
    }

    @Test
    void avoidsCollisionWithExistingTraineeUsername() {
        mockDefaultCreation();
        when(traineeDao.findAll()).thenReturn(List.of(
                new Trainee(1L, "Sara", "Connor", "Sara.Connor", "secret1234", true, null, null)
        ));

        Trainer trainer = trainerService.create(trainer("Sara", "Connor"));

        assertEquals("Sara.Connor1", trainer.getUsername());
        verify(traineeDao, atLeastOnce()).findAll();
    }

    @Test
    void delegatesUpdateSelectAndSelectAll() {
        Trainer trainer = trainerWithId(1L, "Mike", "Tyson");
        when(trainerDao.update(trainer)).thenReturn(true);
        when(trainerDao.findById(1L)).thenReturn(Optional.of(trainer));
        when(trainerDao.findAll()).thenReturn(List.of(trainer));

        assertSame(trainer, trainerService.update(trainer));
        assertSame(trainer, trainerService.select(1L));
        assertEquals(List.of(trainer), trainerService.selectAll());

        verify(trainerDao).update(trainer);
        verify(trainerDao).findById(1L);
        verify(trainerDao, atLeastOnce()).findAll();
    }

    @Test
    void throwsExceptionWhenSelectingNonExistentTrainer() {
        when(trainerDao.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> trainerService.select(999L)
        );

        assertEquals("Trainer not found: 999", exception.getMessage());
    }

    @Test
    void keepsExistingUsernameOutOfCollisionCheck() {
        mockDefaultCreation();
        when(trainerDao.findAll()).thenReturn(List.of(
                new Trainer(1L, "John", "Doe", "John.Doe", "password", true, new TrainingType("Yoga"))
        ));

        Trainer trainer = trainerService.create(trainer("John", "Doe"));

        assertEquals("John.Doe1", trainer.getUsername());
    }

    @Test
    void returnsEmptyListWhenDaoHasNoTrainers() {
        when(trainerDao.findAll()).thenReturn(List.of());

        assertTrue(trainerService.selectAll().isEmpty());

        verify(trainerDao).findAll();
    }

    private void mockDefaultCreation() {
        when(trainerDao.findAll()).thenReturn(List.of());
        when(traineeDao.findAll()).thenReturn(List.of());
        when(trainerDao.create(any(Trainer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    private Trainer trainer(String firstName, String lastName) {
        return trainerWithId(null, firstName, lastName);
    }

    private Trainer trainerWithId(Long id, String firstName, String lastName) {
        return new Trainer(id, firstName, lastName, null, null, true, new TrainingType("Yoga"));
    }
}
