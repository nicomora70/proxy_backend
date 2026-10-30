package com.bovedadocs.model;

public class User {
    private final String id;
    private final String name;
    private final String area;
    private final Role role;

    public User(String id, String name, String area, Role role) {
        this.id = id;
        this.name = name;
        this.area = area;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getArea() {
        return area;
    }

    public Role getRole() {
        return role;
    }
}
