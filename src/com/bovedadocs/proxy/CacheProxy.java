package com.bovedadocs.proxy;

import com.bovedadocs.model.DocumentContent;
import com.bovedadocs.model.DocumentSummary;
import com.bovedadocs.model.User;
import com.bovedadocs.service.DocumentService;
import com.bovedadocs.service.RequestContext;

import java.util.List;
import java.util.Map;

public class CacheProxy extends DocumentProxy {
    private final Map<String, DocumentContent> cache;

    public CacheProxy(DocumentService next, Map<String, DocumentContent> cache) {
        super(next);
        this.cache = cache;
    }

    @Override
    public List<DocumentSummary> list(User user, RequestContext ctx) {
        return next.list(user, ctx);
    }

    @Override
    public DocumentContent read(User user, String documentId, RequestContext ctx) {
        DocumentContent cached = cache.get(documentId);
        if (cached != null) {
            ctx.setCacheHit(true);
            ctx.add("cache", "HIT " + documentId + " served from memory");
            return cached;
        }
        ctx.add("cache", "MISS " + documentId);
        DocumentContent content = next.read(user, documentId, ctx);
        cache.put(documentId, content);
        return content;
    }

    @Override
    public DocumentSummary write(User user, String documentId, String content, RequestContext ctx) {
        DocumentSummary summary = next.write(user, documentId, content, ctx);
        cache.remove(documentId);
        ctx.add("cache", "invalidated " + documentId);
        return summary;
    }

    @Override
    public void delete(User user, String documentId, RequestContext ctx) {
        next.delete(user, documentId, ctx);
        cache.remove(documentId);
        ctx.add("cache", "invalidated " + documentId);
    }
}
