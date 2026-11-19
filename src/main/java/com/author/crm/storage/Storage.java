package com.author.crm.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Storage {
    public static final String TRAINER_NAMESPACE = "TRAINER";
    public static final String TRAINEE_NAMESPACE = "TRAINEE";
    public static final String TRAINING_NAMESPACE = "TRAINING";

    private final Map<String, Map<Long, Object>> data = new ConcurrentHashMap<>();

    public Storage() {
        data.put(TRAINER_NAMESPACE, new ConcurrentHashMap<>());
        data.put(TRAINEE_NAMESPACE, new ConcurrentHashMap<>());
        data.put(TRAINING_NAMESPACE, new ConcurrentHashMap<>());
    }

    @SuppressWarnings("unchecked")
    public <T> Map<Long, T> get(String namespace) {
        return (Map<Long, T>) data.computeIfAbsent(namespace, k -> new ConcurrentHashMap<>());
    }

    public Map<String, Map<Long, Object>> getAllData() {
        return data;
    }
}
