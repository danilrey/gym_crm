package com.author.crm.service;

import com.author.crm.dao.TraineeDao;
import com.author.crm.dao.TrainerDao;
import com.author.crm.model.Trainee;
import com.author.crm.model.Trainer;
import com.author.crm.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {
    @Mock
    private TraineeDao traineeDao;
    @Mock
    private TrainerDao trainerDao;
    private TraineeServiceImpl traineeService;

    @BeforeEach
    void setUp() {
        traineeService = new TraineeServiceImpl();
        traineeService.setTraineeDao(traineeDao);
        traineeService.setTrainerDao(trainerDao);
    }

    @Test
    void createsTraineeWithCredentialsAndDelegatesToDao() {
        mockDefaultCreation();
        Trainee trainee = trainee("John", "Smith");

        Trainee created = traineeService.create(trainee);

        assertSame(trainee, created);
        assertEquals("John.Smith", trainee.getUsername());
        assertEquals(10, trainee.getPassword().length());
        verify(traineeDao).create(trainee);
    }

    @Test
    void avoidsCollisionWithExistingTrainerUsername() {
        mockDefaultCreation();
        when(trainerDao.findAll()).thenReturn(List.of(
                new Trainer(1L, "Alice", "Cooper", "Alice.Cooper", "pass123456", true, "Fitness")
        ));

        Trainee trainee = traineeService.create(trainee("Alice", "Cooper"));

        assertEquals("Alice.Cooper1", trainee.getUsername());
        verify(trainerDao, atLeastOnce()).findAll();
    }

    @Test
    void delegatesUpdateDeleteSelectAndSelectAll() {
        Trainee trainee = traineeWithId(1L, "Bob", "Brown");
        when(traineeDao.update(trainee)).thenReturn(trainee);
        when(traineeDao.findById(1L)).thenReturn(Optional.of(trainee));
        when(traineeDao.findAll()).thenReturn(List.of(trainee));

        assertSame(trainee, traineeService.update(trainee));
        traineeService.delete(1L);
        assertSame(trainee, traineeService.select(1L));
        assertEquals(List.of(trainee), traineeService.selectAll());

        verify(traineeDao).update(trainee);
        verify(traineeDao).deleteById(1L);
        verify(traineeDao).findById(1L);
        verify(traineeDao, atLeastOnce()).findAll();
    }

    @Test
    void throwsExceptionWhenSelectingNonExistentTrainee() {
        when(traineeDao.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> traineeService.select(999L)
        );

        assertEquals("Trainee not found: 999", exception.getMessage());
    }

    @Test
    void keepsExistingUsernameOutOfCollisionCheck() {
        mockDefaultCreation();
        when(traineeDao.findAll()).thenReturn(List.of(
                new Trainee(1L, "John", "Smith", "John.Smith", "password", true, null, null)
        ));

        Trainee trainee = traineeService.create(trainee("John", "Smith"));

        assertEquals("John.Smith1", trainee.getUsername());
    }

    @Test
    void returnsEmptyListWhenDaoHasNoTrainees() {
        when(traineeDao.findAll()).thenReturn(List.of());

        assertTrue(traineeService.selectAll().isEmpty());

        verify(traineeDao).findAll();
    }

    private void mockDefaultCreation() {
        when(traineeDao.findAll()).thenReturn(List.of());
        when(trainerDao.findAll()).thenReturn(List.of());
        when(traineeDao.create(any(Trainee.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    private Trainee trainee(String firstName, String lastName) {
        return traineeWithId(null, firstName, lastName);
    }

    private Trainee traineeWithId(Long id, String firstName, String lastName) {
        return new Trainee(id, firstName, lastName, null, null, true,
                LocalDate.of(1990, 1, 1), "Address");
    }
}
