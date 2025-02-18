package com.mycompany.jobhunter.domain.entity;

import com.mycompany.jobhunter.domain.entity.enumeration.GenderEnum;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @NotBlank(message = "Email is required")
    private String  email;

    @NotBlank(message = "Password is required")
    private String  password;

    private int age;

    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    private String address;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    //company;
    //resumes;
    //role;
}
