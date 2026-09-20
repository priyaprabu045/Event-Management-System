package service;

import dao.AdminDAO;
import dao.UserDAO;
import enums.Role;
import exception.LoginException;
import model.Admin;
import model.User;
import util.InputValidator;

import java.sql.SQLException;

/**
 * Service handling authentication and registration logic for Users and Admins.
 */
public class AuthService {

    private final UserDAO userDAO;
    private final AdminDAO adminDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
        this.adminDAO = new AdminDAO();
    }

    /**
     * Authenticates an account based on email, password, and designated role.
     */
    public User login(String email, String password, Role role) throws LoginException, SQLException {
        if (!InputValidator.isValidEmail(email)) {
            throw new LoginException("Invalid email format. Please enter a valid email address.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new LoginException("Password cannot be empty.");
        }

        if (role == Role.ADMIN) {
            Admin admin = adminDAO.findByEmail(email);
            if (admin == null || !admin.getPassword().equals(password)) {
                throw new LoginException("Invalid Admin credentials. Access denied.");
            }
            return admin;
        } else {
            User user = userDAO.findByEmail(email);
            if (user == null || !user.getPassword().equals(password)) {
                throw new LoginException("Invalid User credentials. Please check your email and password.");
            }
            return user;
        }
    }

    /**
     * Registers a new customer user.
     */
    public User registerUser(String name, String email, String password, String phone) throws LoginException, SQLException {
        if (!InputValidator.isValidString(name)) {
            throw new LoginException("Name cannot be empty.");
        }
        if (!InputValidator.isValidEmail(email)) {
            throw new LoginException("Invalid email format.");
        }
        if (password == null || password.length() < 4) {
            throw new LoginException("Password must be at least 4 characters long.");
        }
        if (!InputValidator.isValidPhone(phone)) {
            throw new LoginException("Invalid phone number. Must be a 10-digit number.");
        }

        if (userDAO.findByEmail(email) != null) {
            throw new LoginException("An account with this email already exists.");
        }

        User newUser = new User(0, name.trim(), email.trim(), password, phone.trim());
        boolean saved = userDAO.save(newUser);
        if (!saved) {
            throw new LoginException("Registration failed due to a database error. Please try again.");
        }
        return newUser;
    }
}
