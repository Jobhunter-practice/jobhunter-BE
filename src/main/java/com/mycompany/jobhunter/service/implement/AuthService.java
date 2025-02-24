package com.mycompany.jobhunter.service.implement;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService implements com.mycompany.jobhunter.service.contract.IAuthService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
    private String googleUserInfoUri;

    @Value("${spring.security.oauth2.client.google.auth.endpoint}")  // or @ConfigurationProperties
    private String googleAuthEndpoint;

    @Override
    public String generateGoogleAuthUrl() {
        String[] scopes = {
                "https://www.googleapis.com/auth/userinfo.profile",
                "https://www.googleapis.com/auth/userinfo.email",
                "https://www.googleapis.com/auth/user.gender.read",
                "https://www.googleapis.com/auth/user.birthday.read",
                "https://www.googleapis.com/auth/user.emails.read",
                "https://www.googleapis.com/auth/directory.readonly",
                "https://www.googleapis.com/auth/user.addresses.read"
        };

        var params = new StringBuilder()
                .append("?client_id=").append(URLEncoder.encode(googleClientId, StandardCharsets.UTF_8))
                .append("&redirect_uri=").append(URLEncoder.encode(googleRedirectUri, StandardCharsets.UTF_8))
                .append("&response_type=code")
                .append("&scope=").append(URLEncoder.encode(String.join(" ", scopes), StandardCharsets.UTF_8))
                .append("&access_type=offline")
                .append("&prompt=consent");

        return googleAuthEndpoint + params;
    }

    @Override
    public Map<String, Object> authenticateAndFetchProfile(String provider, String code) throws IOException {
        if (!"google".equalsIgnoreCase(provider.trim())) {
            throw new IllegalArgumentException("Unsupported provider: " + provider);
        }

        try {
            // Exchange authorization code for access token
            TokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    new GsonFactory(),
                    googleClientId,
                    googleClientSecret,
                    code,
                    googleRedirectUri
            ).execute();

            String accessToken = tokenResponse.getAccessToken();

            RestTemplate restTemplate = new RestTemplate();

            // First get basic profile
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Get basic profile
            String basicProfile = restTemplate.exchange(
                    googleUserInfoUri,
                    HttpMethod.GET,
                    entity,
                    String.class
            ).getBody();

            Map<String, Object> profileData = new ObjectMapper().readValue(
                    basicProfile,
                    new TypeReference<Map<String, Object>>() {}
            );

            // Try to get gender data from People API (requires verification)
            try {
                String genderResponse = restTemplate.exchange(
                        "https://people.googleapis.com/v1/people/me?personFields=genders",
                        HttpMethod.GET,
                        entity,
                        String.class
                ).getBody();

                Map<String, Object> genderData = new ObjectMapper().readValue(
                        genderResponse,
                        new TypeReference<Map<String, Object>>() {}
                );

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
            } catch (Exception e) {
                throw new IOException(e.getMessage());
            }
            Map<String, Object> userData = new HashMap<>();
            userData.put("googleAccountID", profileData.get("sub").toString());
            userData.put("email", profileData.get("email").toString());
            userData.put("name", profileData.get("name").toString());

            userData.put("gender", profileData.get("gender").toString());

            return userData;
        } catch (Exception e) {
            throw new IOException("Failed to authenticate with Google", e);
        }
    }
}
