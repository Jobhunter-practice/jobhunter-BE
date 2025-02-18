package com.mycompany.jobhunter.service;

import com.mycompany.jobhunter.domain.dto.response.ResCreateUserDTO;
import com.mycompany.jobhunter.domain.dto.response.ResUserDTO;
import com.mycompany.jobhunter.domain.dto.response.ResultPaginationDTO;
import com.mycompany.jobhunter.domain.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface UserService {
    public Boolean isEmailExisted(String email);

    public ResCreateUserDTO createUser(User user);

    public User handleGetUserByEmail(String email);

    public Void updateRefreshToken(String refreshToken, String email);

    public User getUserByRefreshTokenAndEmail(String refreshToken, String email);

    public User fetchUserById(long id);

    public Void handleDeleteUserById(long id);

    public ResUserDTO convertToResUserDTO(User user);

    public ResultPaginationDTO fetchAllUsers(Specification<User> spec, Pageable pageable);

}
