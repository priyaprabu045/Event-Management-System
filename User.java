package model;

import enums.Role;
import java.time.LocalDateTime;

/**
 * Model class representing a User entity.
 * Demonstrates Encapsulation and serves as the parent class for Admin (Inheritance).
 */
public class User {

    protected int userId;
    protected String name;
    protected String email;
    protected String password;
    protected String phone;
    protected Role role;
    protected LocalDateTime createdAt;

    public User() {
        this.role = Role.USER;
    }

    public User(int userId, String name, String email, String password, String phone) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = Role.USER;
        this.createdAt = LocalDateTime.now();
    }

    public User(int userId, String name, String email, String password, String phone, LocalDateTime createdAt) {
        this(userId, name, email, password, phone);
        this.createdAt = createdAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Polymorphic method demonstrating role-specific capabilities.
     * Overridden by Admin subclass.
     */
    public String getRoleDescription() {
        return "Customer Account: Has permissions to view events, book tickets, and cancel active bookings.";
    }

    @Override
    public String toString() {
        return "User [ID=" + userId + ", Name=" + name + ", Email=" + email + ", Phone=" + phone + "]";
    }
}
