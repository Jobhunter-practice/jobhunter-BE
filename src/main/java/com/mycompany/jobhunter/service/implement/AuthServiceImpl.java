package com.mycompany.jobhunter.service.implement;

import com.mycompany.jobhunter.domain.dto.oauth.OAuthUserInfo;
import com.mycompany.jobhunter.domain.dto.oauth.OauthUserInfoFactory;
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
    public OAuthUserInfo authenticateAndFetchProfile(String provider, String code) throws IOException {
        try {
            Map<String, Object> userInfo;

            switch (provider.trim().toLowerCase()) {
                case "google":
                    userInfo = authUtil.getUserInfoByGoogleAuthCode(code);
                    break;
                case "github":
                    userInfo = authUtil.getUserInfoByGithubAuthCode(code);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported provider: " + provider);
            }

            if (userInfo == null || userInfo.isEmpty()) {
                throw new IOException("Failed to retrieve user profile data");
            }

            return OauthUserInfoFactory.getOAuthUserInfo(provider.toLowerCase(), userInfo);

        } catch (Exception e) {
            throw new IOException("Failed to authenticate with provider: " + provider, e);
        }
    }

}
