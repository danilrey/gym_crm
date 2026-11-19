package com.author.crm.service;

import com.author.crm.dao.TraineeDao;
import com.author.crm.dao.TrainerDao;
import com.author.crm.model.Trainee;
import com.author.crm.model.Trainer;
import com.author.crm.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsernameGeneratorTest {
    @Mock
    private TraineeDao traineeDao;
    @Mock
    private TrainerDao trainerDao;
    private UsernameGenerator generator;

    @BeforeEach
    void setUp() {
        when(traineeDao.findAll()).thenReturn(List.of());
        when(trainerDao.findAll()).thenReturn(List.of());

        generator = new UsernameGenerator(traineeDao, trainerDao);
    }

    @Test
    void createsWithAvailableUsername() {
        String username = generator.createWithUniqueUsername("Alex", "Jones", value -> value);

        assertEquals("Alex.Jones", username);
    }

    @Test
    void skipsNamesAlreadyUsedByTraineesOrTrainersIgnoringCase() {
        when(traineeDao.findAll()).thenReturn(List.of(trainee("sam.lee")));
        when(trainerDao.findAll()).thenReturn(List.of(trainer("Sam.Lee1")));

        String username = generator.createWithUniqueUsername("Sam", "Lee", value -> value);

        assertEquals("Sam.Lee2", username);
    }

    private Trainee trainee(String username) {
        return new Trainee(null, "Sam", "Lee", username, null, true, null, null);
    }

    private Trainer trainer(String username) {
        return new Trainer(null, "Sam", "Lee", username, null, true, new TrainingType("Fitness"));
    }
}
