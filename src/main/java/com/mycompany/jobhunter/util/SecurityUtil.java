package com.mycompany.jobhunter.util;

import com.mycompany.jobhunter.domain.dto.response.ResLoginDTO;

import com.nimbusds.jose.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SecurityUtil {
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    @Value("${jobhunter.jwt.base64-secret}")
    private String jwtKey;

    @Value("${jobhunter.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;

    @Value("${jobhunter.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    private final JwtEncoder jwtEncoder;

    public SecurityUtil(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String createAccessToken(Long id, String username, String email) {
        ResLoginDTO.UserInsideToken userLogin = new ResLoginDTO.UserInsideToken();
        userLogin.setId(id);
        userLogin.setName(username);
        userLogin.setEmail(email);

        List<String> authorities = new ArrayList<>();
        // TODO: fix hard code for user's permission
        authorities.add("ROLE_USER_CREATE");
        authorities.add("ROLE_USER_UPDATE");

        Instant now = Instant.now();
        Instant expiresAt = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);

        // jwtHeader
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();

        // JwtClaimsSet
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(email)
                .claim("user", userLogin)
                .claim("permission", authorities)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public String createRefreshToken(Long id, String username, String email) {
        ResLoginDTO.UserInsideToken userLogin = new ResLoginDTO.UserInsideToken();
        userLogin.setId(id);
        userLogin.setName(username);
        userLogin.setEmail(email);

        Instant now = Instant.now();
        Instant expiresAt = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);

        // jwtHeader
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();

        // JwtClaimsSet
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(email)
                .claim("user", userLogin)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public static Optional<String> getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }

    public Jwt checkValidRefreshToken(String token){
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
        try {
            return jwtDecoder.decode(token);
        } catch (Exception e) {
            System.out.println(">>> Refresh Token error: " + e.getMessage());
            throw e;
        }
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length,
                JWT_ALGORITHM.getName());
    }
}
