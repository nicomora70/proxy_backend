package com.bovedadocs.model;

public class DocumentSummary {
    private final String id;
    private final String title;
    private final String area;
    private final String ownerId;
    private final String ownerName;
    private final Classification classification;
    private final int sizeKb;
    private final boolean canRead;
    private final boolean canWrite;
    private final boolean canDelete;

    public DocumentSummary(String id, String title, String area, String ownerId, String ownerName,
                           Classification classification, int sizeKb,
                           boolean canRead, boolean canWrite, boolean canDelete) {
        this.id = id;
        this.title = title;
        this.area = area;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.classification = classification;
        this.sizeKb = sizeKb;
        this.canRead = canRead;
        this.canWrite = canWrite;
        this.canDelete = canDelete;
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

    public boolean canRead() {
        return canRead;
    }

    public boolean canWrite() {
        return canWrite;
    }

    public boolean canDelete() {
        return canDelete;
    }
}
