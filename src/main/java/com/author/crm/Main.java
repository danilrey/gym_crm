package com.author.crm;

import com.author.crm.config.AppConfig;
import com.author.crm.facade.Facade;
import com.author.crm.model.Trainee;
import com.author.crm.model.Trainer;
import com.author.crm.model.Training;
import com.author.crm.model.TrainingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            Facade facade = context.getBean(Facade.class);

            LOG.info("Gym CRM started");
            runDemo(facade);
            printStartupSummary(facade);
        }
    }

    private static void runDemo(Facade facade) {
        Trainer trainer = facade.createTrainer(
                new Trainer(null, "Ann", "Lourine", null, null, true, new TrainingType("Functional Training"))
        );
        Trainee firstTrainee = facade.createTrainee(
                new Trainee(null, "Mike", "Smith", null, null, true,
                        LocalDate.of(1998, 6, 15), "Main Street")
        );
        Trainee temporaryTrainee = facade.createTrainee(
                new Trainee(null, "John", "Stinger", null, null, true,
                        LocalDate.of(2000, 3, 10), "Second Street")
        );

        firstTrainee.setAddress("Updated Main Street");
        facade.updateTrainee(firstTrainee);

        Training training = facade.createTraining(
                new Training(null, firstTrainee.getId(), trainer.getId(),
                        "Functional Training Session", new TrainingType("FUNCTIONAL"),
                        LocalDate.now(), 60)
        );

        LOG.info(
                "Created trainer {}, trainee {}, and training {}.",
                trainer.getUsername(),
                facade.selectTraineeProfile(firstTrainee.getId()).getUsername(),
                facade.selectTrainingProfile(training.getId()).getName()
        );

        facade.deleteTrainee(temporaryTrainee.getId());
        LOG.info("Deleted temporary trainee with id {}.", temporaryTrainee.getId());
    }

    private static void printStartupSummary(Facade facade) {
        LOG.info("Trainees:");
        for (Trainee trainee : facade.trainees().selectAll()) {
            LOG.info(
                    "   #{} {} {}, username={}, active={}, address={}",
                    trainee.getId(),
                    trainee.getFirstName(),
                    trainee.getLastName(),
                    trainee.getUsername(),
                    trainee.isActive(),
                    trainee.getAddress()
            );
        }

        LOG.info("Trainers:");
        for (Trainer trainer : facade.trainers().selectAll()) {
            LOG.info(
                    "   #{} {} {}, username={}, active={}, specialization={}",
                    trainer.getId(),
                    trainer.getFirstName(),
                    trainer.getLastName(),
                    trainer.getUsername(),
                    trainer.isActive(),
                    trainer.getSpecialization()
            );
        }

        LOG.info("Trainings:");
        for (Training training : facade.trainings().selectAll()) {
            LOG.info(
                    "   #{} {}, type={}, date={}, duration={} minutes, traineeId={}, trainerId={}",
                    training.getId(),
                    training.getName(),
                    training.getType(),
                    training.getDate(),
                    training.getDuration(),
                    training.getTraineeId(),
                    training.getTrainerId()
            );
        }
    }
}
