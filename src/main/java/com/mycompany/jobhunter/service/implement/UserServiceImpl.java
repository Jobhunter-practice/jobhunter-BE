package com.mycompany.jobhunter.service.implement;

import com.mycompany.jobhunter.domain.dto.response.ResCreateUserDTO;
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

    private ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser com = new ResCreateUserDTO.CompanyUser();

        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());

        return res;
    }

    @Override
    public Boolean isEmailExisted(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public ResCreateUserDTO createUser(User user) {
        ResCreateUserDTO userDTO = convertToResCreateUserDTO(this.userRepository.save(user));
        return userDTO;
    }

    @Override
    public User handleGetUserByUsername(String email) {
        return userRepository.findByEmail(email);
    }
}
