package com.author.crm.service.impl;

import com.author.crm.dao.TraineeDao;
import com.author.crm.dao.TrainerDao;
import com.author.crm.model.Trainee;
import com.author.crm.service.CredentialGenerator;
import com.author.crm.service.TraineeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraineeServiceImpl implements TraineeService {
    private static final Logger LOG = LoggerFactory.getLogger(TraineeServiceImpl.class);
    private static final String TRAINEE_NOT_FOUND_TEMPLATE = "Trainee not found: %d";
    private TraineeDao dao;
    private TrainerDao trainerDao;

    public TraineeServiceImpl() {
    }

    @Autowired
    public void setTraineeDao(TraineeDao dao) {
        this.dao = dao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Override
    public Trainee create(Trainee trainee) {
        initCredentials(trainee);
        LOG.info("Creating trainee profile: {}", trainee.getUsername());

        return dao.create(trainee);
    }

    @Override
    public Trainee update(Trainee trainee) {
        LOG.info("Updating trainee profile: {}", trainee.getId());

        return dao.update(trainee);
    }

    @Override
    public void delete(Long id) {
        LOG.info("Deleting trainee profile: {}", id);
        dao.deleteById(id);
    }

    @Override
    public Trainee select(Long id) {
        LOG.info("Selecting trainee profile: {}", id);

        return dao.findById(id).orElseThrow(() -> new IllegalArgumentException(TRAINEE_NOT_FOUND_TEMPLATE.formatted(id)));
    }

    @Override
    public List<Trainee> selectAll() {
        LOG.info("Selecting all trainee profiles");

        return dao.findAll();
    }

    private void initCredentials(Trainee trainee) {
        trainee.setUsername(CredentialGenerator.username(
                trainee.getFirstName(),
                trainee.getLastName(),
                this::isUsernameTaken
        ));
        trainee.setPassword(CredentialGenerator.password());
    }

    private boolean isUsernameTaken(String username) {
        boolean existsInTrainees = dao.findAll().stream()
                .anyMatch(t -> t.getUsername() != null && t.getUsername().equalsIgnoreCase(username));
        boolean existsInTrainers = trainerDao.findAll().stream()
                .anyMatch(t -> t.getUsername() != null && t.getUsername().equalsIgnoreCase(username));

        return existsInTrainees || existsInTrainers;
    }
}
