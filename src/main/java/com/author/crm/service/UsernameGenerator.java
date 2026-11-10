package com.author.crm.service;

import com.author.crm.dao.TraineeDao;
import com.author.crm.dao.TrainerDao;
import com.author.crm.model.User;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Function;
import java.util.Set;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class UsernameGenerator {
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;

    public UsernameGenerator(TraineeDao traineeDao, TrainerDao trainerDao) {
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
    }

    public synchronized <T> T createWithUniqueUsername(String firstName, String lastName, Function<String, T> creator) {
        Set<String> usernames = Stream.concat(traineeDao.findAll().stream(), trainerDao.findAll().stream())
                .map(User::getUsername)
                .filter(Objects::nonNull)
                .map(username -> username.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
        String username = CredentialGenerator.username(firstName, lastName,
                candidate -> usernames.contains(candidate.toLowerCase(Locale.ROOT)));

        return creator.apply(username);
    }
}
