package com.author.crm.dao;

import com.author.crm.model.Trainer;

public interface TrainerDao extends CrudDao<Trainer> {
    Trainer update(Trainer trainer);
}
