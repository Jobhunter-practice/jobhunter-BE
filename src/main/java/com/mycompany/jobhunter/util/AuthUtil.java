package com.mycompany.jobhunter.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthUtil {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String githubClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String githubClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${spring.security.oauth2.client.registration.github.redirect-uri}")
    private String githubRedirectUri;

    @Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
    private String googleUserInfoUri;

    @Value("${spring.security.oauth2.client.google.auth.endpoint}")
    private String googleAuthEndpoint;

    public Map<String, Object> getUserInfoByGoogleAuthCode(String code) throws IOException {
        try {
            // Exchange authorization code for access token
            TokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(new NetHttpTransport(), new GsonFactory(), googleClientId, googleClientSecret, code, googleRedirectUri).execute();
            String accessToken = tokenResponse.getAccessToken();

            RestTemplate restTemplate = new RestTemplate();

            // First get basic profile
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Get basic profile
            String basicProfile = restTemplate.exchange(googleUserInfoUri, HttpMethod.GET, entity, String.class).getBody();

            Map<String, Object> profileData = new ObjectMapper().readValue(basicProfile, new TypeReference<Map<String, Object>>() {});

            // Try to get gender data from People API
            String genderResponse = restTemplate.exchange("https://people.googleapis.com/v1/people/me?personFields=genders", HttpMethod.GET, entity, String.class).getBody();

            Map<String, Object> genderData = new ObjectMapper().readValue(genderResponse, new TypeReference<Map<String, Object>>() {});

            // Merge gender data into profile data if available
            if (genderData != null && genderData.containsKey("genders")) {
                List<Map<String, Object>> genders = (List<Map<String, Object>>) genderData.get("genders");
                if (!genders.isEmpty()) {
                    Map<String, Object> firstGender = genders.get(0);
                    if (firstGender.containsKey("value")) {
                        profileData.put("gender", firstGender.get("value"));
                    } else {
                        profileData.put("gender", "Unknown");
                    }
                }
            }

            return profileData;
        } catch (Exception e) {
            throw new IOException("Failed to authenticate with Google", e);
        }
    }

    public Map<String, Object> getUserInfoByGithubAuthCode(String code) throws IOException {
        try {
            // Exchange authorization code for access token
            String requestBody = String.format("client_id=%s&client_secret=%s&code=%s", githubClientId, githubClientSecret, code);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Accept", "application/json");

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> tokenResponse = restTemplate.exchange(
                    "https://github.com/login/oauth/access_token",
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            if (tokenResponse.getStatusCode() != HttpStatus.OK || !tokenResponse.getBody().containsKey("access_token")) {
                throw new RuntimeException("Failed to fetch access token from GitHub");
            }

            // Retrieve access token
            String accessToken = (String) tokenResponse.getBody().get("access_token");

            // Set up authorization header for subsequent requests
            HttpHeaders authHeaders = new HttpHeaders();
            authHeaders.setBearerAuth(accessToken);
            HttpEntity<String> authorizedRequest = new HttpEntity<>(authHeaders);

            // Retrieve user info
            ResponseEntity<Map> userInfoResponse = restTemplate.exchange(
                    "https://api.github.com/user",
                    HttpMethod.GET,
                    authorizedRequest,
                    Map.class
            );

            if (userInfoResponse.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Failed to fetch user info from GitHub");
            }

            // Retrieve user emails
            ResponseEntity<List<Map<String, Object>>> emailsResponse = restTemplate.exchange(
                    "https://api.github.com/user/emails",
                    HttpMethod.GET,
                    authorizedRequest,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            // Find primary email and add it to user info
            Map<String, Object> userInfo = userInfoResponse.getBody();

            if (emailsResponse.getStatusCode() == HttpStatus.OK && emailsResponse.getBody() != null) {
                Optional<Map<String, Object>> primaryEmailEntry = emailsResponse.getBody().stream()
                        .filter(e -> e.get("primary").equals(Boolean.TRUE))
                        .findFirst();

                primaryEmailEntry.ifPresent(entry -> {
                    String email = (String) entry.get("email");
                    userInfo.put("email", email);
                });
            }

            return userInfo;

        } catch (Exception e) {
            throw new IOException("Failed to authenticate with GitHub", e);
        }
    }
}
