package com.author.crm.service.impl;

import com.author.crm.dao.TraineeDao;
import com.author.crm.model.Trainee;
import com.author.crm.service.TraineeService;
import com.author.crm.service.UsernameGenerator;
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
    private UsernameGenerator usernameGenerator;

    public TraineeServiceImpl() {
    }

    @Autowired
    public void setTraineeDao(TraineeDao dao) {
        this.dao = dao;
    }

    @Autowired
    public void setUsernameGenerator(UsernameGenerator usernameGenerator) {
        this.usernameGenerator = usernameGenerator;
    }

    @Override
    public Trainee create(Trainee trainee) {
        return usernameGenerator.createWithUniqueUsername(trainee.getFirstName(), trainee.getLastName(), username -> {
            trainee.setUsername(username);
            trainee.setPassword(com.author.crm.service.CredentialGenerator.password());
            LOG.info("Creating trainee profile: {}", username);

            return dao.create(trainee);
        });
    }

    @Override
    public Trainee update(Trainee trainee) {
        LOG.info("Updating trainee profile: {}", trainee.getId());

        if (!dao.update(trainee)) {
            LOG.warn("Cannot update missing trainee profile: {}", trainee.getId());

            throw new IllegalArgumentException(TRAINEE_NOT_FOUND_TEMPLATE.formatted(trainee.getId()));
        }

        return trainee;
    }

    @Override
    public void delete(Long id) {
        if (dao.deleteById(id)) {
            LOG.info("Deleted trainee profile: {}", id);
        } else {
            LOG.warn("Cannot delete missing trainee profile: {}", id);

            throw new IllegalArgumentException(TRAINEE_NOT_FOUND_TEMPLATE.formatted(id));
        }
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

}
