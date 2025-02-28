package com.mycompany.jobhunter.service;

import com.mycompany.jobhunter.domain.dto.oauth.OAuthUserInfo;
import com.mycompany.jobhunter.service.implement.AuthServiceImpl;
import com.mycompany.jobhunter.util.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private AuthUtil authUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set mock values for @Value fields
        ReflectionTestUtils.setField(authService, "googleClientId", "test-google-client-id");
        ReflectionTestUtils.setField(authService, "githubClientId", "test-github-client-id");
        ReflectionTestUtils.setField(authService, "googleRedirectUri", "http://localhost/google/callback");
        ReflectionTestUtils.setField(authService, "githubRedirectUri", "http://localhost/github/callback");
        ReflectionTestUtils.setField(authService, "googleAuthEndpoint", "https://accounts.google.com/o/oauth2/v2/auth");
    }

    @Test
    void generateGoogleAuthUrl_ShouldReturnCorrectUrl() {
        String url = authService.generateGoogleAuthUrl();

        assertTrue(url.contains("client_id=test-google-client-id"));
        assertTrue(url.contains("redirect_uri=http%3A%2F%2Flocalhost%2Fgoogle%2Fcallback"));
        assertTrue(url.contains("response_type=code"));
        assertTrue(url.contains("scope="));
    }

    @Test
    void generateGithubAuthUrl_ShouldReturnCorrectUrl() {
        String url = authService.generateGithubAuthUrl();

        assertEquals("https://github.com/login/oauth/authorize?client_id=test-github-client-id&scope=user,user:email", url);
    }

    @Test
    void authenticateAndFetchProfile_WithGoogle_ShouldReturnUserData() throws IOException {
        // Mock Google profile response
        Map<String, Object> mockProfile = new HashMap<>();
        mockProfile.put("sub", "google-id-123");
        mockProfile.put("email", "test@example.com");
        mockProfile.put("name", "John Doe");
        mockProfile.put("gender", "male");

        when(authUtil.getUserInfoByGoogleAuthCode("valid-code")).thenReturn(mockProfile);

        OAuthUserInfo userData = authService.authenticateAndFetchProfile("google", "valid-code");

        assertEquals("test@example.com", userData.getEmail());
        assertEquals("John Doe", userData.getName());
        assertEquals("male", userData.getGender());
    }

    @Test
    void authenticateAndFetchProfile_WithGithub_ShouldReturnUserData() throws IOException {
        // Mock GitHub profile response
        Map<String, Object> mockProfile = new HashMap<>();
        mockProfile.put("id", "github-id-456");
        mockProfile.put("email", "github@example.com");
        mockProfile.put("name", "Jane Doe");

        when(authUtil.getUserInfoByGithubAuthCode("valid-code")).thenReturn(mockProfile);

        OAuthUserInfo userData = authService.authenticateAndFetchProfile("github", "valid-code");

        assertEquals("github@example.com", userData.getEmail());
        assertEquals("Jane Doe", userData.getName());
    }

    @Test
    void authenticateAndFetchProfile_WithInvalidProvider_ShouldThrowException() {
        Exception exception = assertThrows(IOException.class, () -> {
            authService.authenticateAndFetchProfile("unknown", "some-code");
        });

        assertTrue(exception.getMessage().contains("Failed to authenticate with provider:"));
    }

    @Test
    void authenticateAndFetchProfile_WithEmptyProfile_ShouldThrowException() throws IOException {
        when(authUtil.getUserInfoByGoogleAuthCode("invalid-code")).thenReturn(new HashMap<>());

        IOException exception = assertThrows(IOException.class, () -> {
            authService.authenticateAndFetchProfile("google", "invalid-code");
        });

        assertTrue(exception.getMessage().contains("Failed to authenticate with provider"));
    }

    @Test
    void authenticateAndFetchProfile_WithException_ShouldThrowIOException() throws IOException {
        when(authUtil.getUserInfoByGoogleAuthCode("error-code")).thenThrow(new RuntimeException("API error"));

        IOException exception = assertThrows(IOException.class, () -> {
            authService.authenticateAndFetchProfile("google", "error-code");
        });

        assertTrue(exception.getMessage().contains("Failed to authenticate with provider"));
    }

}