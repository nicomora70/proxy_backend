package com.bovedadocs.audit;

import com.bovedadocs.model.User;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AuditLog {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final List<AuditEntry> entries = new ArrayList<>();
    private int nextId = 1;

    public synchronized AuditEntry add(User user, String action, String documentId,
                                        String status, String message, long latencyMs) {
        AuditEntry entry = new AuditEntry(
                nextId++,
                LocalTime.now().format(FORMAT),
                user.getId(),
                user.getName(),
                action,
                documentId,
                status,
                message,
                latencyMs
        );
        entries.add(entry);
        if (entries.size() > 200) {
            entries.remove(0);
        }
        return entry;
    }

    public synchronized List<AuditEntry> last(int count) {
        int from = Math.max(0, entries.size() - count);
        return new ArrayList<>(entries.subList(from, entries.size()));
    }

    public synchronized void clear() {
        entries.clear();
    }
}
