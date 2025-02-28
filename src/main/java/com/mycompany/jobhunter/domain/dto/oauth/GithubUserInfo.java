package com.mycompany.jobhunter.domain.dto.oauth;

import java.util.Map;

public class GithubUserInfo implements OAuthUserInfo {
    private Map<String, Object> attributes;

    public GithubUserInfo(Map<String, Object> attributes) {
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
        return "unsupported info";
    }

    @Override
    public String getProvider() {
        return "Github";
    }
}
