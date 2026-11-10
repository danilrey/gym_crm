package com.author.crm.service;

import com.author.crm.dao.TrainingDao;
import com.author.crm.dao.TraineeDao;
import com.author.crm.dao.TrainerDao;
import com.author.crm.model.Training;
import com.author.crm.model.TrainingType;
import com.author.crm.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {
    @Mock
    private TrainingDao trainingDao;
    @Mock
    private TraineeDao traineeDao;
    @Mock
    private TrainerDao trainerDao;
    private TrainingServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new TrainingServiceImpl();
        service.setTrainingDao(trainingDao);
        service.setTraineeDao(traineeDao);
        service.setTrainerDao(trainerDao);
    }

    @Test
    void delegatesCreateSelectAndSelectAll() {
        Training training = training(1L);
        when(traineeDao.findById(1L)).thenReturn(Optional.of(new com.author.crm.model.Trainee()));
        when(trainerDao.findById(2L)).thenReturn(Optional.of(new com.author.crm.model.Trainer()));
        when(trainingDao.create(training)).thenReturn(training);
        when(trainingDao.findById(1L)).thenReturn(Optional.of(training));
        when(trainingDao.findAll()).thenReturn(List.of(training));

        assertSame(training, service.create(training));
        assertSame(training, service.select(1L));
        assertEquals(List.of(training), service.selectAll());

        verify(trainingDao).create(training);
        verify(trainingDao).findById(1L);
        verify(trainingDao).findAll();
    }

    @Test
    void throwsExceptionWhenSelectingNonExistentTraining() {
        when(trainingDao.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> service.select(999L)
        );

        assertEquals("Training not found: 999", exception.getMessage());
    }

    @Test
    void returnsEmptyListWhenDaoHasNoTrainings() {
        when(trainingDao.findAll()).thenReturn(List.of());

        assertTrue(service.selectAll().isEmpty());

        verify(trainingDao).findAll();
    }

    @Test
    void returnsDaoResultWhenCreatingTraining() {
        Training requested = training(null);
        Training persisted = training(7L);
        when(traineeDao.findById(1L)).thenReturn(Optional.of(new com.author.crm.model.Trainee()));
        when(trainerDao.findById(2L)).thenReturn(Optional.of(new com.author.crm.model.Trainer()));
        when(trainingDao.create(requested)).thenReturn(persisted);

        assertSame(persisted, service.create(requested));

        verify(trainingDao).create(requested);
    }

    private Training training(Long id) {
        return new Training(id, 1L, 2L, "Cardio Blast", new TrainingType("Cardio"),
                LocalDate.of(2026, 5, 1), 45);
    }
}
