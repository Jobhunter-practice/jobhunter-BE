package com.mycompany.jobhunter.service.implement;

import com.mycompany.jobhunter.domain.dto.response.user.ResCreateUserDTO;
import com.mycompany.jobhunter.domain.dto.response.user.ResUpdateUserDTO;
import com.mycompany.jobhunter.domain.dto.response.user.ResUserDTO;
import com.mycompany.jobhunter.domain.dto.response.ResultPaginationDTO;
import com.mycompany.jobhunter.domain.entity.User;
import com.mycompany.jobhunter.domain.entity.enumeration.GenderEnum;
import com.mycompany.jobhunter.repository.RoleRepository;
import com.mycompany.jobhunter.repository.UserRepository;
import com.mycompany.jobhunter.service.contract.IUserService;
import com.mycompany.jobhunter.util.SecurityUtil;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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
        user.setRole(this.roleRepository.findByName("NORMAL_USER"));
        ResCreateUserDTO userDTO = convertToResCreateUserDTO(this.userRepository.save(user));
        return userDTO;
    }

    @Override
    public User handleGetUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Void updateRefreshToken(String refreshToken, String email) {
        User user = userRepository.findByEmail(email);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        return null;
    }

    @Override
    public User getUserByRefreshTokenAndEmail(String refreshToken, String email) {
        return userRepository.findByRefreshTokenAndEmail(refreshToken, email);
    }

    @Override
    public User fetchUserById(long id) {
        Optional<User> res = userRepository.findById(id);
        if(res.isPresent()) {
            return res.get();
        }
        return null;
    }

    @Override
    public Void handleDeleteUserById(long id) {
        userRepository.deleteById(id);
        return null;
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();

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
    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();

        res.setId(user.getId());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        return res;
    }

    @Override
    public ResultPaginationDTO fetchAllUsers(Specification<User> spec, Pageable pageable) {
        if (pageable == null) {
            pageable = Pageable.unpaged();
        }
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        if (pageUser == null) {
            pageUser = Page.empty();
        }

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        if (pageable.isUnpaged()) {
            mt.setPage(1);
            mt.setPageSize(pageUser.getSize());
        } else {
            mt.setPage(pageable.getPageNumber() + 1);
            mt.setPageSize(pageable.getPageSize());
        }

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());
        rs.setMeta(mt);

        List<ResUserDTO> listUser = pageUser.getContent().stream()
                .map(this::convertToResUserDTO)
                .collect(Collectors.toList());

        rs.setResult(listUser);

        return rs;
    }

    @Override
    public User handleUpdateUser(User reqUser) {
        User currentUser = this.fetchUserById(reqUser.getId());
        if (currentUser != null) {
            currentUser.setAddress(reqUser.getAddress());
            currentUser.setGender(reqUser.getGender());
            currentUser.setAge(reqUser.getAge());
            currentUser.setName(reqUser.getName());
            currentUser.setUpdatedAt(Instant.now());

            // update
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    @Override
    public User getOrCreateUser(String email, Map<String, Object> userInfo) {
        try {
            User user = handleGetUserByEmail(email);
            if (user != null) {
                return user;
            }

            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(userInfo.get("name").toString());

            Map<String, GenderEnum> genderEnumMap = Map.of(
                    "male", GenderEnum.MALE,
                    "female", GenderEnum.FEMALE
            );

            newUser.setGender(genderEnumMap.get(userInfo.get("gender").toString()));
            newUser.setPassword("socialLoginDefaultPassword");
            newUser.setCreatedAt(Instant.now());
            newUser.setCreatedBy(email);

            try {
                ResCreateUserDTO usr = createUser(newUser);
                User res = new User();
                res.setId(usr.getId());
                res.setEmail(usr.getEmail());
                res.setName(usr.getName());
                res.setAge(usr.getAge());
                res.setUpdatedAt(Instant.now());
                res.setUpdatedBy(email);
                res.setCreatedAt(Instant.now());
                res.setCreatedBy(email);

                return res;
            } catch (DataIntegrityViolationException e) {
                // If another transaction created the user first, fetch and return it
                User existingUser = handleGetUserByEmail(email);
                if (existingUser != null) {
                    return existingUser;
                }
                throw e;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to process user creation", e);
        }
    }
}
