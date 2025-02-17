package com.mycompany.jobhunter.service;

import com.mycompany.jobhunter.domain.dto.UserDTO;
import com.mycompany.jobhunter.domain.entity.User;

public interface UserService {
    public Boolean isEmailExisted(String email);

    public UserDTO createUser(User user);
}
