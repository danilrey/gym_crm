package com.author.crm.service.impl;

import com.author.crm.dao.TraineeDao;
import com.author.crm.dao.TrainerDao;
import com.author.crm.model.Trainer;
import com.author.crm.service.CredentialGenerator;
import com.author.crm.service.TrainerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainerServiceImpl implements TrainerService {
    private static final Logger LOG = LoggerFactory.getLogger(TrainerServiceImpl.class);
    private static final String TRAINER_NOT_FOUND_TEMPLATE = "Trainer not found: %d";
    private TrainerDao dao;
    private TraineeDao traineeDao;

    public TrainerServiceImpl() {
    }

    @Autowired
    public void setTrainerDao(TrainerDao dao) {
        this.dao = dao;
    }

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Override
    public Trainer create(Trainer trainer) {
        initCredentials(trainer);
        LOG.info("Creating trainer profile: {}", trainer.getUsername());

        return dao.create(trainer);
    }

    @Override
    public Trainer update(Trainer trainer) {
        LOG.info("Updating trainer profile: {}", trainer.getId());

        return dao.update(trainer);
    }

    @Override
    public Trainer select(Long id) {
        LOG.info("Selecting trainer profile: {}", id);

        return dao.findById(id).orElseThrow(() -> new IllegalArgumentException(TRAINER_NOT_FOUND_TEMPLATE.formatted(id)));
    }

    @Override
    public List<Trainer> selectAll() {
        LOG.info("Selecting all trainer profiles");

        return dao.findAll();
    }

    private void initCredentials(Trainer trainer) {
        trainer.setUsername(CredentialGenerator.username(
                trainer.getFirstName(),
                trainer.getLastName(),
                this::isUsernameTaken
        ));
        trainer.setPassword(CredentialGenerator.password());
    }

    private boolean isUsernameTaken(String username) {
        boolean existsInTrainers = dao.findAll().stream()
                .anyMatch(t -> t.getUsername() != null && t.getUsername().equalsIgnoreCase(username));
        boolean existsInTrainees = traineeDao.findAll().stream()
                .anyMatch(t -> t.getUsername() != null && t.getUsername().equalsIgnoreCase(username));

        return existsInTrainers || existsInTrainees;
    }
}
