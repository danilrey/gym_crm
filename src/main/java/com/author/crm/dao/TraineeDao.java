package com.author.crm.dao;

import com.author.crm.model.Trainee;

public interface TraineeDao extends CrudDao<Trainee> {
    Trainee update(Trainee trainee);

    void deleteById(Long id);
}
