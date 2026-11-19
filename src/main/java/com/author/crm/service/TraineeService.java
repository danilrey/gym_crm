package com.author.crm.service;

import com.author.crm.model.Trainee;

import java.util.List;

public interface TraineeService {
    Trainee create(Trainee trainee);

    Trainee update(Trainee trainee);

    void delete(Long id);

    Trainee select(Long id);

    List<Trainee> selectAll();
}
