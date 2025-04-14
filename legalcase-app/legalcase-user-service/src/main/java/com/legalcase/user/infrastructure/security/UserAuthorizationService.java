package com.legalcase.user.infrastructure.security;

import com.legalcase.commons.security.SecurityUtils;
import com.legalcase.user.domain.entity.User;
import com.legalcase.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for user authorization checks
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserAuthorizationService {
    
    private final UserRepository userRepository;
    
    /**
     * Check if the current user is the user with the given ID
     */
    @Transactional(readOnly = true)
    public boolean isCurrentUser(Long userId) {
        log.debug("Checking if current user has ID: {}", userId);
        
        return SecurityUtils.getCurrentUsername()
                .flatMap(username -> userRepository.findByUsername(username))
                .map(User::getId)
                .map(id -> id.equals(userId))
                .orElse(false);
    }
    
    /**
     * Check if the current user is the user with the given username
     */
    @Transactional(readOnly = true)
    public boolean isCurrentUser(String username) {
        log.debug("Checking if current user has username: {}", username);
        
        return SecurityUtils.getCurrentUsername()
                .map(currentUsername -> currentUsername.equals(username))
                .orElse(false);
    }
} 