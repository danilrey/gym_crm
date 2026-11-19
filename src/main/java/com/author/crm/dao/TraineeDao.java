package com.author.crm.dao;

import com.author.crm.model.Trainee;

public interface TraineeDao extends CrudDao<Trainee> {
    boolean update(Trainee trainee);

    boolean deleteById(Long id);
}
