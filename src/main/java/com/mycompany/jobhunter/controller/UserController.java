package com.mycompany.jobhunter.controller;

import com.mycompany.jobhunter.domain.dto.response.user.ResCreateUserDTO;
import com.mycompany.jobhunter.domain.dto.response.user.ResUpdateUserDTO;
import com.mycompany.jobhunter.domain.dto.response.user.ResUserDTO;
import com.mycompany.jobhunter.domain.dto.response.ResultPaginationDTO;
import com.mycompany.jobhunter.domain.entity.User;
import com.mycompany.jobhunter.service.IUserService;
import com.mycompany.jobhunter.utils.SecurityUtil;
import com.mycompany.jobhunter.utils.errors.IdInvalidException;
import com.mycompany.jobhunter.utils.errors.InvalidRequestBodyException;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final IUserService userService;
    private final SecurityUtil securityUtil;
    private final PasswordEncoder passwordEncoder;

    public UserController(
            IUserService userService,
            PasswordEncoder passwordEncoder,
            SecurityUtil securityUtil
    ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.securityUtil = securityUtil;
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
        Optional<String> currentUserLogin = this.securityUtil.getCurrentUser();
        if (currentUserLogin.isPresent()) {
            user.setCreatedBy(currentUserLogin.get());
        }

        try {
            ResCreateUserDTO createdUser = this.userService.createUser(user);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (Exception ex) {
            String errorMessage = "Internal server error: " + ex.getMessage();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // 500
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        User currentUser = this.userService.fetchUserById(id);
        if (currentUser == null) {
            StringBuilder stringBuilder = new StringBuilder()
                    .append("User with id ")
                    .append(id)
                    .append(" not found");
            throw new IdInvalidException(stringBuilder.toString());
        }

        this.userService.handleDeleteUserById(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) throws IdInvalidException {
        User fetchUser = this.userService.fetchUserById(id);
        if (fetchUser == null) {
            throw new IdInvalidException("User id = " + id + " not found");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(this.userService.convertToResUserDTO(fetchUser));
    }

    @GetMapping("/users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @Filter Specification<User> spec,
            Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(
                this.userService.fetchAllUsers(spec, pageable));
    }

    @PutMapping("/users")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User user) throws IdInvalidException {
        User updatedUser = this.userService.handleUpdateUser(user);
        if (updatedUser == null) {
            throw new IdInvalidException("User id = " + user.getId() + " not found");
        }
        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(updatedUser));
    }
}
