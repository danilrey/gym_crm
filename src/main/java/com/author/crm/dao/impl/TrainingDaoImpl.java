package com.author.crm.dao.impl;

import com.author.crm.dao.TrainingDao;
import com.author.crm.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository("trainingDao")
public class TrainingDaoImpl implements TrainingDao {
    private static final String STORAGE_HAS_NOT_BEEN_INITIALIZED_MESSAGE = "Storage has not been initialized";
    private Map<Long, Training> storage;
    private final AtomicLong idSequence = new AtomicLong(0);

    public TrainingDaoImpl() {
    }

    @Autowired
    @Qualifier("trainingStorage")
    public void setStorage(Map<Long, Training> storage) {
        this.storage = storage;

        syncIdSequence();
    }

    @Override
    public synchronized Training create(Training training) {
        if (storage == null) {
            throw new IllegalStateException(STORAGE_HAS_NOT_BEEN_INITIALIZED_MESSAGE);
        }

        if (training.getId() == null) {
            training.setId(idSequence.incrementAndGet());
        } else {
            idSequence.updateAndGet(current -> Math.max(current, training.getId()));
        }
        storage.put(training.getId(), training);

        return training;
    }

    @Override
    public Optional<Training> findById(Long id) {
        if (storage == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Training> findAll() {
        if (storage == null) {
            return Collections.emptyList();
        }

        return new ArrayList<>(storage.values());
    }

    private void syncIdSequence() {
        if (storage != null && !storage.isEmpty()) {
            long maxId = storage.keySet().stream().mapToLong(Long::longValue).max().orElse(0);

            idSequence.updateAndGet(current -> Math.max(current, maxId));
        }
    }
}
