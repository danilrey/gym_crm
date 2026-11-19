package com.author.crm.dao.impl;

import com.author.crm.dao.TrainingDao;
import com.author.crm.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository("trainingDao")
public class TrainingDaoImpl extends AbstractMapDao<Training> implements TrainingDao {
    public TrainingDaoImpl() {
        super(Training::getId, Training::setId);
    }

    @Autowired
    @Qualifier("trainingStorage")
    public void setStorage(Map<Long, Training> storage) {
        super.initializeStorage(storage);
    }
}
