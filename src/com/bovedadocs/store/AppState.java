package com.bovedadocs.store;

import com.bovedadocs.audit.AuditLog;
import com.bovedadocs.model.Document;
import com.bovedadocs.model.DocumentContent;
import com.bovedadocs.model.User;
import com.bovedadocs.proxy.AccessControlProxy;
import com.bovedadocs.proxy.CacheProxy;
import com.bovedadocs.proxy.LazyLoadProxy;
import com.bovedadocs.proxy.LoggingProxy;
import com.bovedadocs.service.DocumentService;
import com.bovedadocs.service.RemoteDocumentService;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AppState {
    private final Map<String, User> users;
    private final Map<String, Document> documents;
    private final RemoteDocumentService remote;
    private final Map<String, DocumentContent> cache = new ConcurrentHashMap<>();
    private final Map<String, DocumentContent> lazyLoaded = new ConcurrentHashMap<>();
    private final AuditLog audit = new AuditLog();

    public AppState() {
        this.users = SeedData.users();
        this.documents = SeedData.documents();
        this.remote = new RemoteDocumentService(documents, users);
    }

    public Map<String, User> users() {
        return users;
    }

    public User user(String id) {
        return users.get(id);
    }

    public AuditLog audit() {
        return audit;
    }

    public DocumentService chain(String layers) {
        Set<String> enabled = new HashSet<>();
        if (layers != null) {
            for (String layer : layers.split(",")) {
                enabled.add(layer.trim().toLowerCase());
            }
        }
        DocumentService service = remote;
        if (enabled.contains("lazy")) {
            service = new LazyLoadProxy(service, lazyLoaded);
        }
        if (enabled.contains("cache")) {
            service = new CacheProxy(service, cache);
        }
        if (enabled.contains("access")) {
            service = new AccessControlProxy(service, remote);
        }
        if (enabled.contains("log")) {
            service = new LoggingProxy(service, audit);
        }
        return service;
    }

    public synchronized void reset() {
        documents.clear();
        documents.putAll(SeedData.documents());
        cache.clear();
        lazyLoaded.clear();
        audit.clear();
    }
}
