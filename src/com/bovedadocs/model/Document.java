package com.bovedadocs.model;

public class Document {
    private final String id;
    private final String title;
    private final String area;
    private final String ownerId;
    private final String ownerName;
    private final Classification classification;
    private final int sizeKb;
    private String content;

    public Document(String id, String title, String area, String ownerId, String ownerName,
                    Classification classification, int sizeKb, String content) {
        this.id = id;
        this.title = title;
        this.area = area;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.classification = classification;
        this.sizeKb = sizeKb;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArea() {
        return area;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Classification getClassification() {
        return classification;
    }

    public int getSizeKb() {
        return sizeKb;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
