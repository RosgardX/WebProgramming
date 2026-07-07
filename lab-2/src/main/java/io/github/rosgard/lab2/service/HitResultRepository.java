package io.github.rosgard.lab2.service;

import io.github.rosgard.lab2.service.HitService.HitResult;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class HitResultRepository {

    private final List<HitResult> storage = new CopyOnWriteArrayList<>();

    public void add(HitResult result) {
        storage.add(result);
    }

    public List<HitResult> findAll() {
        return storage;
    }

    public List<HitResult> getHistory() {
        return storage;
    }

    public void clear() {
        storage.clear();
    }

    public int size() {
        return storage.size();
    }
}