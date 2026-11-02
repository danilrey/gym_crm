package com.author.crm.dao.impl;

import com.author.crm.dao.TrainerDao;
import com.author.crm.model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository("trainerDao")
public class TrainerDaoImpl implements TrainerDao {
    private static final String STORAGE_HAS_NOT_BEEN_INITIALIZED_MESSAGE = "Storage has not been initialized";
    private static final String TRAINER_DOES_NOT_EXIST_TEMPLATE = "Trainer does not exist: %s";
    private Map<Long, Trainer> storage;
    private final AtomicLong idSequence = new AtomicLong(0);

    public TrainerDaoImpl() {
    }

    @Autowired
    @Qualifier("trainerStorage")
    public void setStorage(Map<Long, Trainer> storage) {
        this.storage = storage;

        syncIdSequence();
    }

    @Override
    public synchronized Trainer create(Trainer trainer) {
        if (storage == null) {
            throw new IllegalStateException(STORAGE_HAS_NOT_BEEN_INITIALIZED_MESSAGE);
        }

        if (trainer.getId() == null) {
            trainer.setId(idSequence.incrementAndGet());
        } else {
            idSequence.updateAndGet(current -> Math.max(current, trainer.getId()));
        }
        storage.put(trainer.getId(), trainer);

        return trainer;
    }

    @Override
    public synchronized Trainer update(Trainer trainer) {
        if (storage == null) {
            throw new IllegalStateException(STORAGE_HAS_NOT_BEEN_INITIALIZED_MESSAGE);
        }
        Long id = trainer.getId();

        if (id == null || !storage.containsKey(id)) {
            throw new IllegalArgumentException(TRAINER_DOES_NOT_EXIST_TEMPLATE.formatted(id));
        }
        storage.put(id, trainer);

        return trainer;
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        if (storage == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Trainer> findAll() {
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
