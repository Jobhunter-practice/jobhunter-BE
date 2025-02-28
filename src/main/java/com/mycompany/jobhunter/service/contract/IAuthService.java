package com.mycompany.jobhunter.service.contract;

import com.mycompany.jobhunter.domain.dto.oauth.OAuthUserInfo;

import java.io.IOException;
import java.util.Map;

public interface IAuthService {
    String generateGoogleAuthUrl();
    String generateGithubAuthUrl();
    OAuthUserInfo authenticateAndFetchProfile(String provider, String code) throws IOException;
}
