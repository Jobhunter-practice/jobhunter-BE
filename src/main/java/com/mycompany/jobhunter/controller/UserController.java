package com.mycompany.jobhunter.controller;

import com.mycompany.jobhunter.domain.dto.response.ResCreateUserDTO;
import com.mycompany.jobhunter.domain.entity.User;
import com.mycompany.jobhunter.service.UserService;
import com.mycompany.jobhunter.utils.errors.InvalidRequestBodyException;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    public ResponseEntity<ResCreateUserDTO> createUser(@Valid @RequestBody User user)
            throws InvalidRequestBodyException {
        // check if user's email has already existed ?
        Boolean isEmailExisted = this.userService.isEmailExisted(user.getEmail());

        if (isEmailExisted) {
            String message = "Email " + user.getEmail() + " already exists";
            throw new InvalidRequestBodyException(message);
        }

        String hashedPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        user.setCreatedAt(Instant.now());
        // TODO: retrive current user from context-holder
        // user.setCreatedBy();
        try {
            ResCreateUserDTO createdUser = this.userService.createUser(user);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (Exception ex) {
            String errorMessage = "Internal server error: " + ex.getMessage();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // 500
        }
    }
}
