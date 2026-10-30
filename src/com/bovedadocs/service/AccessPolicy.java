package com.bovedadocs.service;

import com.bovedadocs.model.Classification;
import com.bovedadocs.model.Role;
import com.bovedadocs.model.User;

public final class AccessPolicy {
    private AccessPolicy() {
    }

    public static boolean canRead(User user, String area, String ownerId, Classification classification) {
        if (user.getRole() == Role.ADMIN) {
            return true;
        }
        if (user.getId().equals(ownerId)) {
            return true;
        }
        if (classification == Classification.PUBLIC) {
            return true;
        }
        return classification == Classification.INTERNAL && user.getArea().equals(area);
    }

    public static boolean canWrite(User user, String area, String ownerId, Classification classification) {
        if (user.getRole() == Role.ADMIN) {
            return true;
        }
        if (user.getRole() == Role.VIEWER) {
            return false;
        }
        if (user.getId().equals(ownerId)) {
            return true;
        }
        return user.getArea().equals(area) && classification != Classification.CONFIDENTIAL;
    }

    public static boolean canDelete(User user) {
        return user.getRole() == Role.ADMIN;
    }
}
