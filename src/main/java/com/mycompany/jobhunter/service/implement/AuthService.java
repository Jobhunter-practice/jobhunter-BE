package com.mycompany.jobhunter.service.implement;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
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

    @Override
    public Map<String, Object> authenticateAndFetchProfile(String provider, String code) throws IOException {
        if (!"google".equalsIgnoreCase(provider.trim())) {
            throw new IllegalArgumentException("Unsupported provider: " + provider);
        }

        try {
            // Exchange authorization code for access token
            String accessToken = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    new GsonFactory(),
                    googleClientId,
                    googleClientSecret,
                    code,
                    googleRedirectUri
            ).execute().getAccessToken();

            // Use access token to fetch user info
            RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            String response = restTemplate.exchange(
                    googleUserInfoUri,
                    HttpMethod.GET,
                    entity,
                    String.class
            ).getBody();

            // Parse JSON response to Map
            return new ObjectMapper().readValue(response, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new IOException("Failed to authenticate with Google", e);
        }
    }
}
