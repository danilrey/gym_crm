package com.author.crm.service;

import com.author.crm.model.Training;

import java.util.List;

public interface TrainingService {
    Training create(Training training);

    Training select(Long id);

    List<Training> selectAll();
}
