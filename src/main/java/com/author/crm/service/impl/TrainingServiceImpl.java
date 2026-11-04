package com.author.crm.service.impl;

import com.author.crm.dao.TrainingDao;
import com.author.crm.model.Training;
import com.author.crm.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingServiceImpl implements TrainingService {
    private static final Logger LOG = LoggerFactory.getLogger(TrainingServiceImpl.class);
    private static final String TRAINING_NOT_FOUND_TEMPLATE = "Training not found: %d";
    private TrainingDao dao;

    public TrainingServiceImpl() {
    }

    @Autowired
    public void setTrainingDao(TrainingDao dao) {
        this.dao = dao;
    }

    @Override
    public Training create(Training training) {
        LOG.info("Creating training profile: {}", training.getName());

        return dao.create(training);
    }

    @Override
    public Training select(Long id) {
        LOG.info("Selecting training profile: {}", id);

        return dao.findById(id).orElseThrow(() -> new IllegalArgumentException(TRAINING_NOT_FOUND_TEMPLATE.formatted(id)));
    }

    @Override
    public List<Training> selectAll() {
        LOG.info("Selecting all training profiles");

        return dao.findAll();
    }
}
