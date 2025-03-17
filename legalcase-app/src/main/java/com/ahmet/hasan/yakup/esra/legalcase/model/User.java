package com.ahmet.hasan.yakup.esra.legalcase.model;

import com.ahmet.hasan.yakup.esra.legalcase.model.enums.UserRole;

/**
 * @brief Model class that holds user information.
 * @author Team
 * @date March 2025
 */
public class User extends BaseEntity {
    private String username;
    private String password;
    private String email;
    private String name;
    private String surname;
    private UserRole role;

    /**
     * @brief Default constructor
     */
    public User() {
        super();
    }

    /**
     * @brief Parameterized constructor
     * @param id User ID
     * @param username Username
     * @param email Email address
     * @param name User's first name
     * @param surname User's last name
     * @param role User role
     */
    public User(Long id, String username, String email, String name, String surname, UserRole role) {
        super(id);
        this.username = username;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.role = role;
    }

    /**
     * @brief Get username
     * @return Username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @brief Set username
     * @param username Username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @brief Get password (hashed)
     * @return Hashed password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @brief Set password
     * @param password Password to set (should be hashed before setting)
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @brief Get email address
     * @return Email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * @brief Set email address
     * @param email Email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @brief Get first name
     * @return First name
     */
    public String getName() {
        return name;
    }

    /**
     * @brief Set first name
     * @param name First name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @brief Get last name
     * @return Last name
     */
    public String getSurname() {
        return surname;
    }

    /**
     * @brief Set last name
     * @param surname Last name to set
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * @brief Get user role
     * @return User role
     */
    public UserRole getRole() {
        return role;
    }

    /**
     * @brief Set user role
     * @param role User role to set
     */
    public void setRole(UserRole role) {
        this.role = role;
    }

    /**
     * @brief Authenticate user with password
     * @param password Password to check
     * @return True if password matches, false otherwise
     */
    public boolean authenticate(String password) {
        // In a real application, this would use proper password hashing
        return this.password != null && this.password.equals(password);
    }

    /**
     * @brief Check if user has a specific role
     * @param requiredRole Role to check
     * @return True if user has the required role, false otherwise
     */
    public boolean hasRole(UserRole requiredRole) {
        return this.role == requiredRole;
    }

    /**
     * @brief String representation of the user
     * @return String representing the user object
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", role=" + role +
                '}';
    }
}