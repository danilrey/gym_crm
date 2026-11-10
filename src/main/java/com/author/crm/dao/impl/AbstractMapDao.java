package com.author.crm.dao.impl;

import com.author.crm.dao.CrudDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Function;

abstract class AbstractMapDao<T> implements CrudDao<T> {
    private static final String STORAGE_MUST_BE_CONFIGURED_MESSAGE = "Storage must be configured";
    private static final String STORAGE_HAS_NOT_BEEN_INITIALIZED_MESSAGE = "Storage has not been initialized";
    private final AtomicLong idSequence = new AtomicLong();
    private final Function<T, Long> idGetter;
    private final BiConsumer<T, Long> idSetter;
    private Map<Long, T> storage;

    AbstractMapDao(Function<T, Long> idGetter, BiConsumer<T, Long> idSetter) {
        this.idGetter = idGetter;
        this.idSetter = idSetter;
    }

    @Override
    public synchronized T create(T entity) {
        Map<Long, T> values = storage();
        Long id = idGetter.apply(entity);

        if (id == null) {
            id = idSequence.incrementAndGet();
            idSetter.accept(entity, id);
        } else {
            long assignedId = id;
            idSequence.updateAndGet(current -> Math.max(current, assignedId));
        }
        values.put(id, entity);

        return entity;
    }

    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(storage().get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage().values());
    }

    protected final void initializeStorage(Map<Long, T> storage) {
        this.storage = Objects.requireNonNull(storage, STORAGE_MUST_BE_CONFIGURED_MESSAGE);
        storage.keySet().stream().filter(Objects::nonNull).mapToLong(Long::longValue).max()
                .ifPresent(max -> idSequence.updateAndGet(current -> Math.max(current, max)));
    }

    protected final Map<Long, T> storage() {
        if (storage == null) {
            throw new IllegalStateException(STORAGE_HAS_NOT_BEEN_INITIALIZED_MESSAGE);
        }

        return storage;
    }
}
