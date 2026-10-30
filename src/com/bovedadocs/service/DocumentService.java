package com.bovedadocs.service;

import com.bovedadocs.model.DocumentContent;
import com.bovedadocs.model.DocumentSummary;
import com.bovedadocs.model.User;

import java.util.List;

public interface DocumentService {
    List<DocumentSummary> list(User user, RequestContext ctx);

    DocumentContent read(User user, String documentId, RequestContext ctx);

    DocumentSummary write(User user, String documentId, String content, RequestContext ctx);

    void delete(User user, String documentId, RequestContext ctx);
}
