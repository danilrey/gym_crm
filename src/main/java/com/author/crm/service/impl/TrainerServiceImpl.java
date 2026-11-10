package com.author.crm.service.impl;

import com.author.crm.dao.TrainerDao;
import com.author.crm.model.Trainer;
import com.author.crm.service.CredentialGenerator;
import com.author.crm.service.TrainerService;
import com.author.crm.service.UsernameGenerator;
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
    private UsernameGenerator usernameGenerator;

    public TrainerServiceImpl() {
    }

    @Autowired
    public void setTrainerDao(TrainerDao dao) {
        this.dao = dao;
    }

    @Autowired
    public void setUsernameGenerator(UsernameGenerator usernameGenerator) {
        this.usernameGenerator = usernameGenerator;
    }

    @Override
    public Trainer create(Trainer trainer) {
        return usernameGenerator.createWithUniqueUsername(trainer.getFirstName(), trainer.getLastName(), username -> {
            trainer.setUsername(username);
            trainer.setPassword(CredentialGenerator.password());
            LOG.info("Creating trainer profile: {}", username);

            return dao.create(trainer);
        });
    }

    @Override
    public Trainer update(Trainer trainer) {
        LOG.info("Updating trainer profile: {}", trainer.getId());

        if (!dao.update(trainer)) {
            LOG.warn("Cannot update missing trainer profile: {}", trainer.getId());

            throw new IllegalArgumentException(TRAINER_NOT_FOUND_TEMPLATE.formatted(trainer.getId()));
        }

        return trainer;
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

}
