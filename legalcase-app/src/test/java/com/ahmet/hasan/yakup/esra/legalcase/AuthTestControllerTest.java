package com.ahmet.hasan.yakup.esra.legalcase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.ahmet.hasan.yakup.esra.legalcase.api.AuthTestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for AuthTestController using Mockito instead of Spring context
 * This isolates tests from Spring context loading issues
 */
@ExtendWith(MockitoExtension.class)
public class AuthTestControllerTest {

    @InjectMocks
    private AuthTestController authTestController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authTestController).build();
    }

    @Test
    public void testPublicEndpoint() {
        // Act
        Map<String, String> result = authTestController.publicEndpoint();

        // Assert
        assertEquals("This is a public endpoint", result.get("message"));
    }

    @Test
    public void testAuthenticatedEndpoint() {
        // Arrange
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "testuser",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        Map<String, Object> result = authTestController.authenticatedEndpoint();

        // Assert
        assertEquals("This is a protected endpoint requiring authentication", result.get("message"));
        assertEquals("testuser", result.get("username"));
        assertEquals(Collections.singletonList("ROLE_USER"), result.get("authorities"));

        // Cleanup
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testAdminEndpoint() {
        // Act
        Map<String, String> result = authTestController.adminEndpoint();

        // Assert
        assertEquals("This is an admin endpoint", result.get("message"));
    }

    @Test
    public void testLawyerEndpoint() {
        // Act
        Map<String, String> result = authTestController.lawyerEndpoint();

        // Assert
        assertEquals("This is a lawyer endpoint", result.get("message"));
    }

    @Test
    public void testJudgeEndpoint() {
        // Act
        Map<String, String> result = authTestController.judgeEndpoint();

        // Assert
        assertEquals("This is a judge endpoint", result.get("message"));
    }
}