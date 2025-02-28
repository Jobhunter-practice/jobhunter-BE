package com.mycompany.jobhunter.domain.dto.oauth;

import java.util.Map;

public class OauthUserInfoFactory {

    public static OAuthUserInfo getOAuthUserInfo(String provider, Map<String, Object> attributes) {
        return switch (provider.toLowerCase()) {
            case "google" -> new GoogleUserInfo(attributes);
            case "github" -> new GithubUserInfo(attributes);
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        };
    }
}
