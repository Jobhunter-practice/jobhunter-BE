package com.mycompany.jobhunter.domain.dto.response;

import com.mycompany.jobhunter.domain.entity.enumeration.GenderEnum;

import lombok.Setter;
import lombok.Getter;

import java.time.Instant;

@Getter
@Setter
public class ResUpdateUserDTO {
    private long id;
    private String name;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant updatedAt;

    private CompanyUser company;

    @Getter
    @Setter
    public static class CompanyUser {
        private long id;
        private String name;
    }
}
