package com.bovedadocs.audit;

public class AuditEntry {
    private final int id;
    private final String time;
    private final String userId;
    private final String userName;
    private final String action;
    private final String documentId;
    private final String status;
    private final String message;
    private final long latencyMs;

    public AuditEntry(int id, String time, String userId, String userName, String action,
                      String documentId, String status, String message, long latencyMs) {
        this.id = id;
        this.time = time;
        this.userId = userId;
        this.userName = userName;
        this.action = action;
        this.documentId = documentId;
        this.status = status;
        this.message = message;
        this.latencyMs = latencyMs;
    }

    public int getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getAction() {
        return action;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public long getLatencyMs() {
        return latencyMs;
    }
}
