package com.mycompany.jobhunter.repository;

import com.mycompany.jobhunter.domain.entity.Company;
import com.mycompany.jobhunter.domain.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Boolean existsByEmail(String email);
    User findByEmail(String email);
    User findByRefreshTokenAndEmail(String refreshToken, String email);
    List<User> findByCompany(Company company);

    @EntityGraph(attributePaths = {"company", "role"})
    Page<User> findAll(Specification<User> spec, Pageable pageable);
}
