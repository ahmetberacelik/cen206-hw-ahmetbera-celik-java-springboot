package com.ahmet.hasan.yakup.esra.legalcase.api;

import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.UserRole;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IUserService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing User entities
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    /**
     * POST /users : Create a new user
     *
     * @param user the user to create
     * @return the ResponseEntity with status 201 (Created) and with body the new user, or with status 400 (Bad Request) if the user has already an ID
     */
    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody User user) {
        logger.info("REST request to create a new user");
        ApiResponse<User> response = userService.createUser(user);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.CREATED : HttpStatus.valueOf(response.getErrorCode()));
    }

    /**
     * GET /users : Get all users
     *
     * @return the ResponseEntity with status 200 (OK) and the list of users in body
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        logger.info("REST request to get all users");
        ApiResponse<List<User>> response = userService.getAllUsers();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * GET /users/{id} : Get user by id
     *
     * @param id the id of the user to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the user, or with status 404 (Not Found)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        logger.info("REST request to get user by ID: {}", id);
        ApiResponse<User> response = userService.getUserById(id);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.OK : HttpStatus.valueOf(response.getErrorCode()));
    }

    /**
     * GET /users/username/{username} : Get user by username
     *
     * @param username the username of the user to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the user, or with status 404 (Not Found)
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<User>> getUserByUsername(@PathVariable String username) {
        logger.info("REST request to get user by username: {}", username);
        ApiResponse<User> response = userService.getUserByUsername(username);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.OK : HttpStatus.valueOf(response.getErrorCode()));
    }

    /**
     * GET /users/email/{email} : Get user by email
     *
     * @param email the email of the user to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the user, or with status 404 (Not Found)
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<User>> getUserByEmail(@PathVariable String email) {
        logger.info("REST request to get user by email: {}", email);
        ApiResponse<User> response = userService.getUserByEmail(email);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.OK : HttpStatus.valueOf(response.getErrorCode()));
    }

    /**
     * GET /users/keycloak/{keycloakId} : Get user by Keycloak ID
     *
     * @param keycloakId the Keycloak ID of the user to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the user, or with status 404 (Not Found)
     */
    @GetMapping("/keycloak/{keycloakId}")
    public ResponseEntity<ApiResponse<User>> getUserByKeycloakId(@PathVariable String keycloakId) {
        logger.info("REST request to get user by Keycloak ID: {}", keycloakId);
        ApiResponse<User> response = userService.getUserByKeycloakId(keycloakId);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.OK : HttpStatus.valueOf(response.getErrorCode()));
    }

    /**
     * GET /users/role/{role} : Get users by role
     *
     * @param role the role to filter users by
     * @return the ResponseEntity with status 200 (OK) and the list of users in body
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse<List<User>>> getUsersByRole(@PathVariable UserRole role) {
        logger.info("REST request to get users by role: {}", role);
        ApiResponse<List<User>> response = userService.getUsersByRole(role);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.OK : HttpStatus.valueOf(response.getErrorCode()));
    }

    /**
     * GET /users/search : Search users by name or surname
     *
     * @param term the search term
     * @return the ResponseEntity with status 200 (OK) and the list of users in body
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<User>>> searchUsers(@RequestParam String term) {
        logger.info("REST request to search users with term: {}", term);
        ApiResponse<List<User>> response = userService.searchUsers(term);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.OK : HttpStatus.valueOf(response.getErrorCode()));
    }

    /**
     * PUT /users/{id} : Update existing user
     *
     * @param id the id of the user to update
     * @param user the user to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated user,
     * or with status 400 (Bad Request) if the user is not valid,
     * or with status 500 (Internal Server Error) if the user couldn't be updated
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id, @RequestBody User user) {
        logger.info("REST request to update user with ID: {}", id);

        if (user.getId() == null) {
            user.setId(id);
        } else if (!user.getId().equals(id)) {
            return new ResponseEntity<>(
                    ApiResponse.error("ID in the URL does not match the ID in the request body",
                            HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST);
        }

        ApiResponse<User> response = userService.updateUser(user);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.OK : HttpStatus.valueOf(response.getErrorCode()));
    }

    /**
     * DELETE /users/{id} : Delete user
     *
     * @param id the id of the user to delete
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        logger.info("REST request to delete user with ID: {}", id);
        ApiResponse<Void> response = userService.deleteUser(id);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.NO_CONTENT : HttpStatus.valueOf(response.getErrorCode()));
    }

    /**
     * POST /users/{id}/change-password : Change user password
     *
     * @param id the id of the user
     * @param passwords object containing current and new password
     * @return the ResponseEntity with status 200 (OK)
     */
    @PostMapping("/{id}/change-password")
    public ResponseEntity<ApiResponse<User>> changePassword(@PathVariable Long id,
                                                            @RequestBody Map<String, String> passwords) {
        logger.info("REST request to change password for user with ID: {}", id);

        String currentPassword = passwords.get("currentPassword");
        String newPassword = passwords.get("newPassword");

        if (currentPassword == null || newPassword == null) {
            return new ResponseEntity<>(
                    ApiResponse.error("Both currentPassword and newPassword are required",
                            HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST);
        }

        ApiResponse<User> response = userService.changePassword(id, currentPassword, newPassword);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.OK : HttpStatus.valueOf(response.getErrorCode()));
    }

    /**
     * PUT /users/{id}/enabled : Enable or disable user
     *
     * @param id the id of the user
     * @param enabled the enabled status
     * @return the ResponseEntity with status 200 (OK)
     */
    @PutMapping("/{id}/enabled")
    public ResponseEntity<ApiResponse<User>> setUserEnabled(@PathVariable Long id,
                                                            @RequestParam boolean enabled) {
        logger.info("REST request to set enabled status to {} for user with ID: {}", enabled, id);
        ApiResponse<User> response = userService.setUserEnabled(id, enabled);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.OK : HttpStatus.valueOf(response.getErrorCode()));
    }
}