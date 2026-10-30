package com.bovedadocs.proxy;

import com.bovedadocs.model.DocumentContent;
import com.bovedadocs.model.DocumentSummary;
import com.bovedadocs.model.User;
import com.bovedadocs.service.DocumentService;
import com.bovedadocs.service.RequestContext;

import java.util.List;
import java.util.Map;

public class LazyLoadProxy extends DocumentProxy {
    private final Map<String, DocumentContent> loaded;

    public LazyLoadProxy(DocumentService next, Map<String, DocumentContent> loaded) {
        super(next);
        this.loaded = loaded;
    }

    @Override
    public List<DocumentSummary> list(User user, RequestContext ctx) {
        ctx.add("lazy", "metadata request does not load content");
        return next.list(user, ctx);
    }

    @Override
    public DocumentContent read(User user, String documentId, RequestContext ctx) {
        DocumentContent content = loaded.get(documentId);
        if (content != null) {
            ctx.setLazyReused(true);
            ctx.add("lazy", "REUSE " + documentId + " heavy content already loaded");
            return content;
        }
        ctx.add("lazy", "LOAD " + documentId + " on first read");
        content = next.read(user, documentId, ctx);
        loaded.put(documentId, content);
        return content;
    }

    @Override
    public DocumentSummary write(User user, String documentId, String content, RequestContext ctx) {
        DocumentSummary summary = next.write(user, documentId, content, ctx);
        loaded.remove(documentId);
        ctx.add("lazy", "invalidated " + documentId);
        return summary;
    }

    @Override
    public void delete(User user, String documentId, RequestContext ctx) {
        next.delete(user, documentId, ctx);
        loaded.remove(documentId);
        ctx.add("lazy", "invalidated " + documentId);
    }
}
