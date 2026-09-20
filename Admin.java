package model;

import enums.Role;
import java.time.LocalDateTime;

/**
 * Model class representing an Administrator.
 * Demonstrates Inheritance (extends User) and Polymorphism (overrides getRoleDescription).
 */
public class Admin extends User {

    public Admin() {
        super();
        this.role = Role.ADMIN;
    }

    public Admin(int adminId, String name, String email, String password) {
        super(adminId, name, email, password, "N/A");
        this.role = Role.ADMIN;
    }

    public Admin(int adminId, String name, String email, String password, LocalDateTime createdAt) {
        super(adminId, name, email, password, "N/A", createdAt);
        this.role = Role.ADMIN;
    }

    public int getAdminId() {
        return this.userId;
    }

    public void setAdminId(int adminId) {
        this.userId = adminId;
    }

    @Override
    public String getRoleDescription() {
        return "Administrator Account: Has full management permissions for events, users, bookings, and system configurations.";
    }

    @Override
    public String toString() {
        return "Admin [ID=" + userId + ", Name=" + name + ", Email=" + email + "]";
    }
}
