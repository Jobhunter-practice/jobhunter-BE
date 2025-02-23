package com.mycompany.jobhunter.service.contract;

import java.io.IOException;
import java.util.Map;

public interface IAuthService {
    Map<String, Object> authenticateAndFetchProfile(String provider, String code) throws IOException;
}
