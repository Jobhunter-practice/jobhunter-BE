package com.mycompany.jobhunter.controller;

import com.mycompany.jobhunter.domain.dto.request.ReqLoginDTO;
import com.mycompany.jobhunter.domain.dto.response.user.ResCreateUserDTO;
import com.mycompany.jobhunter.domain.dto.response.ResLoginDTO;
import com.mycompany.jobhunter.domain.entity.User;
import com.mycompany.jobhunter.service.contract.IUserService;
import com.mycompany.jobhunter.util.SecurityUtil;
import com.mycompany.jobhunter.util.error.DuplicatedKeyException;
import com.mycompany.jobhunter.util.error.MissingCookiesException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final IUserService userService;
    private final SecurityUtil securityUtil;
    private final PasswordEncoder passwordEncoder;

    @Value("${jobhunter.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(
            AuthenticationManagerBuilder authenticationManagerBuilder,
            IUserService userService, SecurityUtil securityUtil,
            PasswordEncoder passwordEncoder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userService = userService;
        this.securityUtil = securityUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO user) {
        // Inject username and password
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), // In this case, I use "email" prop at "loadUserByUsername"
                user.getPassword());

        // Get authentication by spring security (override **loadUserByUsername**)
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // Store user info to context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO result = new ResLoginDTO();

        User currentUserDB = userService.handleGetUserByEmail(user.getUsername());
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                currentUserDB.getId(),
                currentUserDB.getEmail(),
                currentUserDB.getName()
        );

        result.setUser(userLogin);

        // create access_token
        String accessToken = securityUtil.createAccessToken(
                currentUserDB.getId(),
                currentUserDB.getName(),
                currentUserDB.getEmail()
        );
        result.setAccessToken(accessToken);

        // create refresh_token
        String refreshToken = securityUtil.createRefreshToken(
                currentUserDB.getId(),
                currentUserDB.getName(),
                currentUserDB.getEmail()
        );
        result.setAccessToken(refreshToken);
        // update user's refresh token
        this.userService.updateRefreshToken(refreshToken, currentUserDB.getEmail());

        // set cookies
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/").maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(result);
    }

    @GetMapping("auth/account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUser().isPresent()
                ? SecurityUtil.getCurrentUser().get()
                : "";

        User currentUserDB = userService.handleGetUserByEmail(email);
        ResLoginDTO.UserGetAccount result = new ResLoginDTO.UserGetAccount();
        if(currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
            userLogin.setEmail(email);
            userLogin.setName(currentUserDB.getName());
            userLogin.setId(currentUserDB.getId());

            result.setUser(userLogin);
        }

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/auth/refresh")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "refresh") String refreshToken
    ) throws MissingCookiesException {
        if(refreshToken.equals("refresh")) {
            throw new MissingCookiesException("Refresh token not found, check your cookies");
        }

        // Check valid
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refreshToken);
        String email = decodedToken.getSubject();
        User userDB = userService.getUserByRefreshTokenAndEmail(refreshToken, email);

        if (userDB == null) {
            throw new MissingCookiesException("Invalid refresh token");
        }

        // issue new token/set refresh token as cookies
        ResLoginDTO res = new ResLoginDTO();

        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                userDB.getId(),
                userDB.getEmail(),
                userDB.getName()
        );
        res.setUser(userLogin);

        // create access token
        String access_token = this.securityUtil.createAccessToken(userDB.getId(), userDB.getName(), userDB.getEmail());
        res.setAccessToken(access_token);

        // create refresh token
        String new_refresh_token = this.securityUtil.createRefreshToken(userDB.getId(), userDB.getName(), userDB.getEmail());

        // update user
        this.userService.updateRefreshToken(new_refresh_token, email);

        // set cookies
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", new_refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(res);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout() throws BadCredentialsException {
        String email = SecurityUtil.getCurrentUser().isPresent()
                ? SecurityUtil.getCurrentUser().get()
                : "";

        if(email.equals("")) {
            throw new BadCredentialsException("Bad credentials");
        }

        // update user's refresh token
        this.userService.updateRefreshToken(null, email);

        // remove refresh token cookie
        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .body(null);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<ResCreateUserDTO> register(
            @Valid @RequestBody User postUser
    ) throws DuplicatedKeyException {
        Boolean isEmailExist = this.userService.isEmailExisted(postUser.getEmail());
        if(isEmailExist){
            StringBuilder errorMsg = new StringBuilder("Email: ")
                    .append(postUser.getEmail())
                    .append(" already exists");
            throw new DuplicatedKeyException(errorMsg.toString());
        }

        String hashPassword = this.passwordEncoder.encode(postUser.getPassword());
        postUser.setPassword(hashPassword);
        postUser.setCreatedAt(Instant.now());
        postUser.setCreatedBy(postUser.getEmail());
        ResCreateUserDTO createdUser = this.userService.createUser(postUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
}
