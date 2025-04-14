package com.legalcase.user.application.service.impl;

import com.legalcase.commons.exception.BusinessException;
import com.legalcase.commons.exception.ResourceNotFoundException;
import com.legalcase.user.api.dto.request.CreateUserRequest;
import com.legalcase.user.api.dto.response.UserResponse;
import com.legalcase.user.application.service.UserService;
import com.legalcase.user.domain.entity.User;
import com.legalcase.user.domain.repository.UserRepository;
import com.legalcase.user.infrastructure.keycloak.KeycloakService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of UserService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final KeycloakService keycloakService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating user with username: {}", request.getUsername());
        
        // Validate if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already exists");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }
        
        // Create user in Keycloak
        String keycloakId = keycloakService.createUser(request);
        
        // Create user in database
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .keycloakId(keycloakId)
                .enabled(true)
                .roles(new HashSet<>())
                .build();
        
        // Add roles to the user
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            request.getRoles().forEach(user::addRole);
            keycloakService.assignRolesToUser(keycloakId, request.getRoles());
        }
        
        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.info("Getting user by id: {}", id);
        return userRepository.findById(id)
                .map(this::mapToUserResponse)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        log.info("Getting user by username: {}", username);
        return userRepository.findByUsername(username)
                .map(this::mapToUserResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User with username " + username + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        log.info("Getting user by email: {}", email);
        return userRepository.findByEmail(email)
                .map(this::mapToUserResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserByKeycloakId(String keycloakId) {
        log.info("Getting user by keycloak id: {}", keycloakId);
        return userRepository.findByKeycloakId(keycloakId)
                .map(this::mapToUserResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("Getting all users");
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, CreateUserRequest request) {
        log.info("Updating user with id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, id));
        
        // Validate if username or email already exists for another user
        if (!user.getUsername().equals(request.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already exists");
        }
        
        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }
        
        // Update user in Keycloak
        keycloakService.updateUser(user.getKeycloakId(), request);
        
        // Update user in database
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        
        // Update roles
        if (request.getRoles() != null) {
            // Clear existing roles
            user.getRoles().clear();
            
            // Add new roles
            request.getRoles().forEach(user::addRole);
            
            // Update roles in Keycloak
            keycloakService.updateUserRoles(user.getKeycloakId(), request.getRoles());
        }
        
        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, id));
        
        // Delete user from Keycloak
        keycloakService.deleteUser(user.getKeycloakId());
        
        // Delete user from database
        userRepository.delete(user);
    }
    
    /**
     * Maps a User entity to a UserResponse DTO
     */
    private UserResponse mapToUserResponse(User user) {
        UserResponse response = modelMapper.map(user, UserResponse.class);
        
        // Map roles
        response.setRoles(user.getRoles().stream()
                .map(role -> role.getRoleName())
                .collect(Collectors.toSet()));
        
        return response;
    }
} 