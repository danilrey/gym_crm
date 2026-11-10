package com.author.crm.config;

import com.author.crm.model.Trainee;
import com.author.crm.model.Trainer;
import com.author.crm.model.Training;
import com.author.crm.model.TrainingType;
import com.author.crm.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;

@Component
public class StorageDataLoader implements BeanPostProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(StorageDataLoader.class);
    private static final String PREFIX = "#";
    private static final String REGEX = ",";
    private static final String TRAINER = "TRAINER";
    private static final String TRAINEE = "TRAINEE";
    private static final String TRAINING = "TRAINING";
    private static final String UNABLE_TO_LOAD_INITIAL_CRM_DATA_FROM_TEMPLATE = "Unable to load initial CRM data from %s";

    private Resource dataFile;

    public StorageDataLoader() {
    }

    @Value("${storage.data.file}")
    public void setDataFile(Resource dataFile) {
        this.dataFile = dataFile;
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) {
        if (bean instanceof Storage storage) {
            load(storage);
        }

        return bean;
    }

    public void load(Storage storage) {
        if (dataFile == null || !dataFile.exists()) {
            LOG.warn("Storage data file is not found or null: {}", dataFile);

            return;
        }

        Map<Long, Trainer> trainers = storage.get(Storage.TRAINER_NAMESPACE);
        Map<Long, Trainee> trainees = storage.get(Storage.TRAINEE_NAMESPACE);
        Map<Long, Training> trainings = storage.get(Storage.TRAINING_NAMESPACE);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(dataFile.getInputStream(), StandardCharsets.UTF_8))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank() || line.stripLeading().startsWith(PREFIX)) {
                    continue;
                }

                String[] p = line.split(REGEX, -1);

                switch (p[0].trim()) {
                    case TRAINER -> {
                        Trainer trainer = new Trainer(
                                Long.valueOf(p[1]),
                                p[2],
                                p[3],
                                p[4],
                                p[5],
                                Boolean.parseBoolean(p[6]),
                                new TrainingType(p[7])
                        );

                        trainers.put(trainer.getId(), trainer);
                    }
                    case TRAINEE -> {
                        Trainee trainee = new Trainee(
                                Long.valueOf(p[1]),
                                p[2],
                                p[3],
                                p[4],
                                p[5],
                                Boolean.parseBoolean(p[6]),
                                LocalDate.parse(p[7]),
                                p[8]
                        );

                        trainees.put(trainee.getId(), trainee);
                    }
                    case TRAINING -> {
                        Training training = new Training(
                                Long.valueOf(p[1]),
                                Long.valueOf(p[2]),
                                Long.valueOf(p[3]),
                                p[4],
                                new TrainingType(p[5]),
                                LocalDate.parse(p[6]),
                                Integer.parseInt(p[7])
                        );

                        trainings.put(training.getId(), training);
                    }
                    default -> LOG.warn("Ignoring unknown data record: {}", p[0]);
                }
            }

            LOG.info("Initial CRM data successfully loaded from {} into Storage", dataFile.getFilename());
        } catch (IOException e) {
            throw new IllegalStateException(UNABLE_TO_LOAD_INITIAL_CRM_DATA_FROM_TEMPLATE.formatted(dataFile), e);
        }
    }
}
