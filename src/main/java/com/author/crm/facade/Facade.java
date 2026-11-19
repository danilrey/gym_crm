package com.author.crm.facade;

import com.author.crm.model.Trainee;
import com.author.crm.model.Trainer;
import com.author.crm.model.Training;
import com.author.crm.service.TraineeService;
import com.author.crm.service.TrainerService;
import com.author.crm.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Facade {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @Autowired
    public Facade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public TraineeService trainees() {
        return traineeService;
    }

    public TrainerService trainers() {
        return trainerService;
    }

    public TrainingService trainings() {
        return trainingService;
    }

    public Trainee createTrainee(Trainee trainee) {
        return traineeService.create(trainee);
    }

    public Trainee updateTrainee(Trainee trainee) {
        return traineeService.update(trainee);
    }

    public void deleteTrainee(Long id) {
        traineeService.delete(id);
    }

    public Trainee selectTraineeProfile(Long id) {
        return traineeService.select(id);
    }

    public Trainer createTrainer(Trainer trainer) {
        return trainerService.create(trainer);
    }

    public Trainer updateTrainer(Trainer trainer) {
        return trainerService.update(trainer);
    }

    public Trainer selectTrainerProfile(Long id) {
        return trainerService.select(id);
    }

    public Training createTraining(Training training) {
        return trainingService.create(training);
    }

    public Training selectTrainingProfile(Long id) {
        return trainingService.select(id);
    }
}
