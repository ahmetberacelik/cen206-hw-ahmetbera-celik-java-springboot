package com.ahmet.hasan.yakup.esra.legalcase.api;

import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IUserAuthenticationService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for authentication operations
 */
@RestController
@RequestMapping("/auth")
public class UserAuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(UserAuthenticationController.class);

    private final IUserAuthenticationService authService;

    @Autowired
    public UserAuthenticationController(IUserAuthenticationService authService) {
        this.authService = authService;
    }

    /**
     * POST /auth/register : Register a new user
     *
     * @param user the user to register
     * @return the ResponseEntity with status 201 (Created) and with body the new user
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@RequestBody User user) {
        logger.info("REST request to register a new user: {}", user.getUsername());
        ApiResponse<User> response = authService.registerUser(user);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.CREATED : HttpStatus.valueOf(response.getErrorCode()));
    }

    /**
     * POST /auth/login : Authenticate a user
     *
     * @param credentials the login credentials (username/email and password)
     * @return the ResponseEntity with status 200 (OK) and with the authenticated user
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody Map<String, String> credentials) {
        logger.info("REST request to login user");

        String usernameOrEmail = credentials.get("usernameOrEmail");
        String password = credentials.get("password");

        if (usernameOrEmail == null || password == null) {
            return new ResponseEntity<>(
                    ApiResponse.error("Both usernameOrEmail and password are required",
                            HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST);
        }

        ApiResponse<Map<String, Object>> response = authService.authenticateUser(usernameOrEmail, password);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.OK : HttpStatus.valueOf(response.getErrorCode()));
    }

    /**
     * GET /auth/profile : Get the current authenticated user
     *
     * @return the ResponseEntity with status 200 (OK) and with the current user
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<User>> getCurrentUser(@RequestHeader("Authorization") String token) {
        logger.info("REST request to get current user");
        ApiResponse<User> response = authService.getCurrentUser(token);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.OK : HttpStatus.valueOf(response.getErrorCode()));
    }

    /**
     * POST /auth/logout : Logout the current user
     *
     * @return the ResponseEntity with status 200 (OK)
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String token) {
        logger.info("REST request to logout user");
        ApiResponse<Void> response = authService.logoutUser(token);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.OK : HttpStatus.valueOf(response.getErrorCode()));
    }
}