package com.author.crm.config;

import com.author.crm.model.Trainee;
import com.author.crm.model.Trainer;
import com.author.crm.model.Training;
import com.author.crm.storage.Storage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.Map;

@Configuration
@ComponentScan(basePackages = "com.author.crm")
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public Storage storage() {
        return new Storage();
    }

    @Bean("trainerStorage")
    public Map<Long, Trainer> trainerStorage(Storage storage) {
        return storage.get(Storage.TRAINER_NAMESPACE);
    }

    @Bean("traineeStorage")
    public Map<Long, Trainee> traineeStorage(Storage storage) {
        return storage.get(Storage.TRAINEE_NAMESPACE);
    }

    @Bean("trainingStorage")
    public Map<Long, Training> trainingStorage(Storage storage) {
        return storage.get(Storage.TRAINING_NAMESPACE);
    }
}
