package com.mycompany.jobhunter.repository;

import com.mycompany.jobhunter.domain.entity.Company;
import com.mycompany.jobhunter.domain.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Boolean existsByEmail(String email);
    User findByEmail(String email);
    User findByRefreshTokenAndEmail(String refreshToken, String email);
    List<User> findByCompany(Company company);
}
