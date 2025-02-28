package com.mycompany.jobhunter.service.contract;

import com.mycompany.jobhunter.domain.dto.oauth.OAuthUserInfo;
import com.mycompany.jobhunter.domain.dto.response.user.ResCreateUserDTO;
import com.mycompany.jobhunter.domain.dto.response.user.ResUpdateUserDTO;
import com.mycompany.jobhunter.domain.dto.response.user.ResUserDTO;
import com.mycompany.jobhunter.domain.dto.response.ResultPaginationDTO;
import com.mycompany.jobhunter.domain.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;

public interface IUserService {
    public Boolean isEmailExisted(String email);

    public ResCreateUserDTO createUser(User user);

    public User handleGetUserByEmail(String email);

    public Void updateRefreshToken(String refreshToken, String email);

    public User getUserByRefreshTokenAndEmail(String refreshToken, String email);

    public User fetchUserById(long id);

    public Void handleDeleteUserById(long id);

    public ResUserDTO convertToResUserDTO(User user);

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user);

    public ResultPaginationDTO fetchAllUsers(Specification<User> spec, Pageable pageable);

    public User handleUpdateUser(User user);

    public User getOrCreateUser(String email, OAuthUserInfo userInfo);
}
