package com.bovedadocs.service;

import com.bovedadocs.model.Document;
import com.bovedadocs.model.DocumentContent;
import com.bovedadocs.model.DocumentSummary;
import com.bovedadocs.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RemoteDocumentService implements DocumentService, DocumentCatalog {
    private final Map<String, Document> documents;
    private final Map<String, User> users;

    public RemoteDocumentService(Map<String, Document> documents, Map<String, User> users) {
        this.documents = documents;
        this.users = users;
    }

    public Map<String, Document> documents() {
        return documents;
    }

    public Map<String, User> users() {
        return users;
    }

    @Override
    public List<DocumentSummary> list(User user, RequestContext ctx) {
        pause(180);
        List<DocumentSummary> result = new ArrayList<>();
        for (Document document : documents.values()) {
            result.add(summary(user, document));
        }
        ctx.add("remote", result.size() + " metadata records read");
        return result;
    }

    @Override
    public DocumentContent read(User user, String documentId, RequestContext ctx) {
        Document document = find(documentId);
        pause(1200);
        ctx.add("remote", "content streamed from vault (" + document.getSizeKb() + " KB)");
        return new DocumentContent(document.getId(), document.getTitle(), document.getContent(), document.getSizeKb());
    }

    @Override
    public DocumentSummary write(User user, String documentId, String content, RequestContext ctx) {
        Document document = find(documentId);
        pause(420);
        document.setContent(content);
        ctx.add("remote", "content stored in vault");
        return summary(user, document);
    }

    @Override
    public void delete(User user, String documentId, RequestContext ctx) {
        find(documentId);
        pause(300);
        documents.remove(documentId);
        ctx.add("remote", "record removed from vault");
    }

    @Override
    public DocumentSummary summary(User user, String documentId) {
        return summary(user, find(documentId));
    }

    private DocumentSummary summary(User user, Document document) {
        return new DocumentSummary(
                document.getId(),
                document.getTitle(),
                document.getArea(),
                document.getOwnerId(),
                document.getOwnerName(),
                document.getClassification(),
                document.getSizeKb(),
                AccessPolicy.canRead(user, document.getArea(), document.getOwnerId(), document.getClassification()),
                AccessPolicy.canWrite(user, document.getArea(), document.getOwnerId(), document.getClassification()),
                AccessPolicy.canDelete(user)
        );
    }

    private Document find(String id) {
        Document document = documents.get(id);
        if (document == null) {
            throw new DocumentNotFoundException("Document " + id + " was not found");
        }
        return document;
    }

    private void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Request interrupted");
        }
    }
}
