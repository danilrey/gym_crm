package com.author.crm.dao;

import com.author.crm.model.Trainer;

public interface TrainerDao extends CrudDao<Trainer> {
    boolean update(Trainer trainer);
}
