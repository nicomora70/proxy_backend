package com.bovedadocs.proxy;

import com.bovedadocs.model.DocumentContent;
import com.bovedadocs.model.DocumentSummary;
import com.bovedadocs.model.User;
import com.bovedadocs.service.AccessDeniedException;
import com.bovedadocs.service.AccessPolicy;
import com.bovedadocs.service.DocumentCatalog;
import com.bovedadocs.service.DocumentService;
import com.bovedadocs.service.RequestContext;

import java.util.ArrayList;
import java.util.List;

public class AccessControlProxy extends DocumentProxy {
    private final DocumentCatalog catalog;

    public AccessControlProxy(DocumentService next, DocumentCatalog catalog) {
        super(next);
        this.catalog = catalog;
    }

    @Override
    public List<DocumentSummary> list(User user, RequestContext ctx) {
        ctx.add("access", "evaluating policy for " + user.getName());
        List<DocumentSummary> all = next.list(user, ctx);
        List<DocumentSummary> visible = new ArrayList<>();
        for (DocumentSummary document : all) {
            if (AccessPolicy.canRead(user, document.getArea(), document.getOwnerId(), document.getClassification())) {
                visible.add(document);
            }
        }
        ctx.add("access", visible.size() + " of " + all.size() + " records visible for " + user.getName());
        return visible;
    }

    @Override
    public DocumentContent read(User user, String documentId, RequestContext ctx) {
        DocumentSummary document = catalog.summary(user, documentId);
        if (!AccessPolicy.canRead(user, document.getArea(), document.getOwnerId(), document.getClassification())) {
            ctx.add("access", "DENIED read on " + documentId + " for " + user.getName());
            throw new AccessDeniedException(user.getName() + " cannot read " + documentId);
        }
        ctx.add("access", "read allowed on " + documentId);
        return next.read(user, documentId, ctx);
    }

    @Override
    public DocumentSummary write(User user, String documentId, String content, RequestContext ctx) {
        DocumentSummary document = catalog.summary(user, documentId);
        if (!AccessPolicy.canWrite(user, document.getArea(), document.getOwnerId(), document.getClassification())) {
            ctx.add("access", "DENIED write on " + documentId + " for " + user.getName());
            throw new AccessDeniedException(user.getName() + " cannot write " + documentId);
        }
        ctx.add("access", "write allowed on " + documentId);
        return next.write(user, documentId, content, ctx);
    }

    @Override
    public void delete(User user, String documentId, RequestContext ctx) {
        catalog.summary(user, documentId);
        if (!AccessPolicy.canDelete(user)) {
            ctx.add("access", "DENIED delete on " + documentId + " for " + user.getName());
            throw new AccessDeniedException(user.getName() + " cannot delete " + documentId);
        }
        ctx.add("access", "delete allowed on " + documentId);
        next.delete(user, documentId, ctx);
    }
}
