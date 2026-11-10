package com.author.crm.dao.impl;

import com.author.crm.dao.TrainerDao;
import com.author.crm.model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository("trainerDao")
public class TrainerDaoImpl extends AbstractMapDao<Trainer> implements TrainerDao {
    public TrainerDaoImpl() {
        super(Trainer::getId, Trainer::setId);
    }

    @Autowired
    @Qualifier("trainerStorage")
    public void setStorage(Map<Long, Trainer> storage) {
        super.initializeStorage(storage);
    }

    @Override
    public synchronized boolean update(Trainer trainer) {
        Long id = trainer.getId();

        if (id == null || findById(id).isEmpty()) {
            return false;
        }
        storage().put(id, trainer);

        return true;
    }
}
