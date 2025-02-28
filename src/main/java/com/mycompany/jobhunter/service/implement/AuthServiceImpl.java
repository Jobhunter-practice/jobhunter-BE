package com.mycompany.jobhunter.service.implement;

import com.mycompany.jobhunter.util.AuthUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements com.mycompany.jobhunter.service.contract.IAuthService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String githubClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String githubClientSecret;

    @Value("${spring.security.oauth2.client.registration.github.redirect-uri}")
    private String githubRedirectUri;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
    private String googleUserInfoUri;

    @Value("${spring.security.oauth2.client.google.auth.endpoint}")  // or @ConfigurationProperties
    private String googleAuthEndpoint;

    private AuthUtil authUtil;

    public AuthServiceImpl(AuthUtil authUtil) {
        this.authUtil = authUtil;
    }

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

        String params = "?client_id=" + URLEncoder.encode(googleClientId, StandardCharsets.UTF_8) +
                "&redirect_uri=" + URLEncoder.encode(googleRedirectUri, StandardCharsets.UTF_8) +
                "&response_type=code" +
                "&scope=" + URLEncoder.encode(String.join(" ", scopes), StandardCharsets.UTF_8) +
                "&access_type=offline" +
                "&prompt=consent";

        return googleAuthEndpoint + params;
    }

    @Override
    public String generateGithubAuthUrl() {
        return String.format(
                "https://github.com/login/oauth/authorize?client_id=%s&scope=user,user:email",
                githubClientId
        );
    }

    @Override
    public Map<String, Object> authenticateAndFetchProfile(String provider, String code) throws IOException {
        try {
            Map<String, Object> profileData = new HashMap<>();

            switch (provider.trim().toLowerCase()) {
                case "google":
                    profileData = this.authUtil.getUserInfoByGoogleAuthCode(code);
                    break;
                case "github":
                    profileData = this.authUtil.getUserInfoByGithubAuthCode(code);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported provider: " + provider);
            }

            if (profileData == null || profileData.isEmpty()) {
                throw new IOException("Failed to retrieve user profile data");
            }

            Map<String, Object> userData = new HashMap<>();
            userData.put("googleAccountID", profileData.getOrDefault("sub", "Unknown").toString());
            userData.put("email", profileData.getOrDefault("email", "Unknown").toString());
            userData.put("name", profileData.getOrDefault("name", "Unknown").toString());
            userData.put("gender", profileData.getOrDefault("gender", "Unknown").toString());

            return userData;
        } catch (Exception e) {
            throw new IOException("Failed to authenticate with provider: " + provider, e);
        }
    }

}
