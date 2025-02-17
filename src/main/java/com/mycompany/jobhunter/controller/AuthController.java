package com.mycompany.jobhunter.controller;

import com.mycompany.jobhunter.domain.dto.request.ReqUserDTO;
import com.mycompany.jobhunter.domain.dto.response.ResCreateUserDTO;
import com.mycompany.jobhunter.domain.dto.response.ResLoginDTO;
import com.mycompany.jobhunter.domain.entity.User;
import com.mycompany.jobhunter.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;

    @Value("${jobhunter.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@RequestBody ReqUserDTO user) {
        // Inject username and password
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                user.getUsername(), // In this case, I use "email" prop at "loadUserByUsername"
                user.getPassword());

        // Get authentication by spring security (override **loadUserByUsername**)
        Authentication authentication = authenticationManagerBuilder.getObject()
                                            .authenticate(authenticationToken);
        // Store user info to context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO result = new ResLoginDTO();

        User currentUserDB = userService.handleGetUserByUsername(user.getUsername());
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId(),
                    currentUserDB.getEmail(),
                    currentUserDB.getName());

            result.setUser(userLogin);
        }

        // create access_token
        String accessToken = UUID.randomUUID().toString();
        // create refresh_token
        String refreshToken = UUID.randomUUID().toString();
        // update user

        // set cookies
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(123)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(result);
    }
}
