package com.bovedadocs.proxy;

import com.bovedadocs.audit.AuditLog;
import com.bovedadocs.model.DocumentContent;
import com.bovedadocs.model.DocumentSummary;
import com.bovedadocs.model.User;
import com.bovedadocs.service.AccessDeniedException;
import com.bovedadocs.service.DocumentService;
import com.bovedadocs.service.RequestContext;

import java.util.List;

public class LoggingProxy extends DocumentProxy {
    private final AuditLog audit;

    public LoggingProxy(DocumentService next, AuditLog audit) {
        super(next);
        this.audit = audit;
    }

    @Override
    public List<DocumentSummary> list(User user, RequestContext ctx) {
        long start = System.nanoTime();
        try {
            List<DocumentSummary> documents = next.list(user, ctx);
            audit.add(user, "LIST", null, "OK", documents.size() + " records returned", elapsed(start));
            return documents;
        } catch (RuntimeException exception) {
            audit.add(user, "LIST", null, status(exception), exception.getMessage(), elapsed(start));
            throw exception;
        }
    }

    @Override
    public DocumentContent read(User user, String documentId, RequestContext ctx) {
        long start = System.nanoTime();
        try {
            DocumentContent content = next.read(user, documentId, ctx);
            audit.add(user, "READ", documentId, "OK", "content delivered", elapsed(start));
            return content;
        } catch (RuntimeException exception) {
            audit.add(user, "READ", documentId, status(exception), exception.getMessage(), elapsed(start));
            throw exception;
        }
    }

    @Override
    public DocumentSummary write(User user, String documentId, String content, RequestContext ctx) {
        long start = System.nanoTime();
        try {
            DocumentSummary summary = next.write(user, documentId, content, ctx);
            audit.add(user, "WRITE", documentId, "OK", "content updated", elapsed(start));
            return summary;
        } catch (RuntimeException exception) {
            audit.add(user, "WRITE", documentId, status(exception), exception.getMessage(), elapsed(start));
            throw exception;
        }
    }

    @Override
    public void delete(User user, String documentId, RequestContext ctx) {
        long start = System.nanoTime();
        try {
            next.delete(user, documentId, ctx);
            audit.add(user, "DELETE", documentId, "OK", "record removed", elapsed(start));
        } catch (RuntimeException exception) {
            audit.add(user, "DELETE", documentId, status(exception), exception.getMessage(), elapsed(start));
            throw exception;
        }
    }

    private long elapsed(long start) {
        return (System.nanoTime() - start) / 1_000_000;
    }

    private String status(RuntimeException exception) {
        return exception instanceof AccessDeniedException ? "DENIED" : "ERROR";
    }
}
