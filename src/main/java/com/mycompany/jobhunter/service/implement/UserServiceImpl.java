package com.mycompany.jobhunter.service.implement;

import com.mycompany.jobhunter.domain.dto.UserDTO;
import com.mycompany.jobhunter.domain.entity.User;
import com.mycompany.jobhunter.repository.UserRepository;
import com.mycompany.jobhunter.service.UserService;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO(
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getGender(),
                user.getAddress()
        );
        return userDTO;
    }

    @Override
    public Boolean isEmailExisted(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserDTO createUser(User user) {
        UserDTO userDTO = convertToUserDTO(this.userRepository.save(user));
        return userDTO;
    }
}
