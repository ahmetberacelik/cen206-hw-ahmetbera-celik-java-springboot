package com.ahmet.hasan.yakup.esra.legalcase.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for testing authentication and authorization
 */
@RestController
@RequestMapping("/auth-test")
public class AuthTestController {

    private static final Logger logger = LoggerFactory.getLogger(AuthTestController.class);

    /**
     * Public endpoint that doesn't require authentication
     */
    @GetMapping("/public")
    public Map<String, String> publicEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "This is a public endpoint");
        return response;
    }

    /**
     * Protected endpoint that requires authentication
     */
    @GetMapping("/authenticated")
    public Map<String, Object> authenticatedEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is a protected endpoint requiring authentication");
        response.put("username", auth.getName());
        response.put("authorities", auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        return response;
    }

    /**
     * Admin endpoint that requires ADMIN role
     */
    @GetMapping("/admin")
    @Secured("ROLE_ADMIN")
    public Map<String, String> adminEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "This is an admin endpoint");
        return response;
    }

    /**
     * Lawyer endpoint that requires LAWYER role
     */
    @GetMapping("/lawyer")
    @Secured("ROLE_LAWYER")
    public Map<String, String> lawyerEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "This is a lawyer endpoint");
        return response;
    }

    /**
     * Judge endpoint that requires JUDGE role
     */
    @GetMapping("/judge")
    @Secured("ROLE_JUDGE")
    public Map<String, String> judgeEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "This is a judge endpoint");
        return response;
    }
}