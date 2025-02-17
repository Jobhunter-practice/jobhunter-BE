package com.mycompany.jobhunter.domain.entity;

import com.mycompany.jobhunter.domain.entity.enumeration.GenderEnum;

import java.time.Instant;

public class User {
    private Long id;
    private String name;
    private String  email;
    private String  password;
    private Integer age;
    private GenderEnum gender;
    private String address;
    private String refreshToken;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant createdBy;
    private Instant updatedBy;

    //company;
    //resumes;
    //role;
}
