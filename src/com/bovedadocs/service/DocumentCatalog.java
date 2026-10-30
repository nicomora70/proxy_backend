package com.bovedadocs.service;

import com.bovedadocs.model.DocumentSummary;
import com.bovedadocs.model.User;

public interface DocumentCatalog {
    DocumentSummary summary(User user, String documentId);
}
