package com.mycompany.jobhunter.domain.dto;

import com.mycompany.jobhunter.domain.entity.enumeration.GenderEnum;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String name;
    private String  email;
    private Integer age;

    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    private String address;

}
