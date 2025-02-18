package com.mycompany.jobhunter.service;

import com.mycompany.jobhunter.domain.dto.response.ResultPaginationDTO;
import com.mycompany.jobhunter.domain.entity.Company;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

public interface ICompanyService {

    public Company handleCreateCompany(Company company);
    public ResultPaginationDTO handleGetCompany(Specification<Company> spec, Pageable pageable);
    public Company handleUpdateCompany(Company company);
    public Void handleDeleteCompany(long id);
    public Optional<Company> findById(long id);
}
