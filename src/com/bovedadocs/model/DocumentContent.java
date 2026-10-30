package com.bovedadocs.model;

public class DocumentContent {
    private final String id;
    private final String title;
    private final String content;
    private final int sizeKb;

    public DocumentContent(String id, String title, String content, int sizeKb) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.sizeKb = sizeKb;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getSizeKb() {
        return sizeKb;
    }
}
