package com.mycompany.jobhunter.service;

import com.mycompany.jobhunter.domain.dto.response.ResultPaginationDTO;
import com.mycompany.jobhunter.domain.entity.Company;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface ICompanyService {
    Company handleCreateCompany(Company c);

    ResultPaginationDTO handleGetCompany(Specification<Company> spec, Pageable pageable);

    Company handleUpdateCompany(Company c);

    Void handleDeleteCompany(long id);

    Optional<Company> findById(long id);
}
