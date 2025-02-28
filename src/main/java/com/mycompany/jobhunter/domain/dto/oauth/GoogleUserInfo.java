package com.mycompany.jobhunter.domain.dto.oauth;

import java.util.Map;

public class GoogleUserInfo implements OAuthUserInfo {
    private Map<String, Object> attributes;

    public GoogleUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }

    @Override
    public String getGender() {
        return attributes.get("gender").toString();
    }

    @Override
    public String getProvider() {
        return "Google";
    }
}
