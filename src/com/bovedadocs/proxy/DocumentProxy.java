package com.bovedadocs.proxy;

import com.bovedadocs.service.DocumentService;

public abstract class DocumentProxy implements DocumentService {
    protected final DocumentService next;

    protected DocumentProxy(DocumentService next) {
        this.next = next;
    }
}
