package com.bovedadocs.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RequestContext {
    private final List<String> trace = Collections.synchronizedList(new ArrayList<>());
    private final long startedAt = System.nanoTime();
    private boolean cacheHit;
    private boolean lazyReused;

    public void add(String layer, String message) {
        trace.add(layer.toUpperCase() + " · " + message);
    }

    public List<String> getTrace() {
        return new ArrayList<>(trace);
    }

    public long getLatencyMs() {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }

    public boolean isCacheHit() {
        return cacheHit;
    }

    public void setCacheHit(boolean cacheHit) {
        this.cacheHit = cacheHit;
    }

    public boolean isLazyReused() {
        return lazyReused;
    }

    public void setLazyReused(boolean lazyReused) {
        this.lazyReused = lazyReused;
    }
}
