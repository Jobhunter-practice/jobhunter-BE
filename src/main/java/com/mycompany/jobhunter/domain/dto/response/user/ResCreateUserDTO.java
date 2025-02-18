package com.mycompany.jobhunter.domain.dto.response.user;

import com.mycompany.jobhunter.domain.entity.enumeration.GenderEnum;

import lombok.Data;

import java.time.Instant;

@Data
public class ResCreateUserDTO {
    private long id;
    private String name;
    private String email;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant createdAt;
    private CompanyUser company;

    @Data
    public static class CompanyUser {
        private long id;
        private String name;
    }
}
