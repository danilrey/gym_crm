package com.author.crm.dao.impl;

import com.author.crm.dao.TraineeDao;
import com.author.crm.model.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository("traineeDao")
public class TraineeDaoImpl implements TraineeDao {
    private static final String STORAGE_HAS_NOT_BEEN_INITIALIZED_MESSAGE = "Storage has not been initialized";
    private static final String TRAINEE_DOES_NOT_EXIST_TEMPLATE = "Trainee does not exist: %s";
    private Map<Long, Trainee> storage;
    private final AtomicLong idSequence = new AtomicLong(0);

    public TraineeDaoImpl() {
    }

    @Autowired
    @Qualifier("traineeStorage")
    public void setStorage(Map<Long, Trainee> storage) {
        this.storage = storage;

        syncIdSequence();
    }

    @Override
    public synchronized Trainee create(Trainee trainee) {
        if (storage == null) {
            throw new IllegalStateException(STORAGE_HAS_NOT_BEEN_INITIALIZED_MESSAGE);
        }

        if (trainee.getId() == null) {
            trainee.setId(idSequence.incrementAndGet());
        } else {
            idSequence.updateAndGet(current -> Math.max(current, trainee.getId()));
        }
        storage.put(trainee.getId(), trainee);

        return trainee;
    }

    @Override
    public synchronized Trainee update(Trainee trainee) {
        if (storage == null) {
            throw new IllegalStateException(STORAGE_HAS_NOT_BEEN_INITIALIZED_MESSAGE);
        }
        Long id = trainee.getId();

        if (id == null || !storage.containsKey(id)) {
            throw new IllegalArgumentException(TRAINEE_DOES_NOT_EXIST_TEMPLATE.formatted(id));
        }
        storage.put(id, trainee);

        return trainee;
    }

    @Override
    public void deleteById(Long id) {
        if (storage != null) {
            storage.remove(id);
        }
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        if (storage == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Trainee> findAll() {
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
