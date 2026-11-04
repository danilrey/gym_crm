package com.author.crm;

import com.author.crm.config.AppConfig;
import com.author.crm.facade.Facade;
import com.author.crm.model.Trainee;
import com.author.crm.model.Trainer;
import com.author.crm.model.Training;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            Facade facade = context.getBean(Facade.class);

            System.out.println("Gym CRM started");
            runDemo(facade);
            printStartupSummary(facade);
        }
    }

    private static void runDemo(Facade facade) {
        Trainer trainer = facade.createTrainer(
                new Trainer(null, "Ann", "Lourine", null, null, true, "Functional Training")
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
                        "Functional Training Session", "FUNCTIONAL",
                        LocalDate.now(), 60)
        );

        System.out.printf(
                "Created trainer %s, trainee %s, and training %s.%n",
                trainer.getUsername(),
                facade.selectTraineeProfile(firstTrainee.getId()).getUsername(),
                facade.selectTrainingProfile(training.getId()).getName()
        );

        facade.deleteTrainee(temporaryTrainee.getId());
        System.out.printf("Deleted temporary trainee with id %d.%n", temporaryTrainee.getId());
    }

    private static void printStartupSummary(Facade facade) {
        System.out.println("Trainees:");
        for (Trainee trainee : facade.trainees().selectAll()) {
            System.out.printf(
                    "   #%d %s %s, username=%s, active=%s, address=%s%n",
                    trainee.getId(),
                    trainee.getFirstName(),
                    trainee.getLastName(),
                    trainee.getUsername(),
                    trainee.isActive(),
                    trainee.getAddress()
            );
        }

        System.out.println("Trainers:");
        for (Trainer trainer : facade.trainers().selectAll()) {
            System.out.printf(
                    "   #%d %s %s, username=%s, active=%s, specialization=%s%n",
                    trainer.getId(),
                    trainer.getFirstName(),
                    trainer.getLastName(),
                    trainer.getUsername(),
                    trainer.isActive(),
                    trainer.getSpecialization()
            );
        }

        System.out.println("Trainings:");
        for (Training training : facade.trainings().selectAll()) {
            System.out.printf(
                    "   #%d %s, type=%s, date=%s, duration=%d minutes, traineeId=%d, trainerId=%d%n",
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
