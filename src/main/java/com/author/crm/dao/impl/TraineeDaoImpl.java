package com.author.crm.dao.impl;

import com.author.crm.dao.TraineeDao;
import com.author.crm.model.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository("traineeDao")
public class TraineeDaoImpl extends AbstractMapDao<Trainee> implements TraineeDao {
    public TraineeDaoImpl() {
        super(Trainee::getId, Trainee::setId);
    }

    @Autowired
    @Qualifier("traineeStorage")
    public void setStorage(Map<Long, Trainee> storage) {
        super.initializeStorage(storage);
    }

    @Override
    public synchronized boolean update(Trainee trainee) {
        Long id = trainee.getId();

        if (id == null || findById(id).isEmpty()) {
            return false;
        }
        super.storage().put(id, trainee);

        return true;
    }

    @Override
    public boolean deleteById(Long id) {
        return super.storage().remove(id) != null;
    }
}
