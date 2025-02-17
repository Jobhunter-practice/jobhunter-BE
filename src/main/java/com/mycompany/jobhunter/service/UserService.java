package com.mycompany.jobhunter.service;

import com.mycompany.jobhunter.domain.dto.response.ResCreateUserDTO;
import com.mycompany.jobhunter.domain.entity.User;

public interface UserService {
    public Boolean isEmailExisted(String email);

    public ResCreateUserDTO createUser(User user);

    public User handleGetUserByUsername(String email);
}
