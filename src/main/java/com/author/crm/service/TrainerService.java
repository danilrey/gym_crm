package com.author.crm.service;

import com.author.crm.model.Trainer;

import java.util.List;

public interface TrainerService {
    Trainer create(Trainer trainer);

    Trainer update(Trainer trainer);

    Trainer select(Long id);

    List<Trainer> selectAll();
}
