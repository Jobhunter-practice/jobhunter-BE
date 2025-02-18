package com.mycompany.jobhunter.service.implement;

import com.mycompany.jobhunter.domain.dto.response.ResultPaginationDTO;
import com.mycompany.jobhunter.domain.entity.Company;
import com.mycompany.jobhunter.repository.CompanyRepository;
import com.mycompany.jobhunter.repository.UserRepository;
import com.mycompany.jobhunter.service.contract.ICompanyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompanyServiceImpl implements ICompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyServiceImpl(
            CompanyRepository companyRepository,
            UserRepository userRepository
    ) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Company handleCreateCompany(Company c) {
        return this.companyRepository.save(c);
    }

    @Override
    public ResultPaginationDTO handleGetCompany(Specification<Company> spec, Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageCompany.getTotalPages());
        mt.setTotal(pageCompany.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageCompany.getContent());
        return rs;
    }

    @Override
    public Company handleUpdateCompany(Company c) {
        Optional<Company> companyOptional = this.companyRepository.findById(c.getId());
        if (companyOptional.isPresent()) {
            Company currentCompany = companyOptional.get();
            currentCompany.setLogo(c.getLogo());
            currentCompany.setName(c.getName());
            currentCompany.setDescription(c.getDescription());
            currentCompany.setAddress(c.getAddress());
            return this.companyRepository.save(currentCompany);
        }
        return null;
    }

    @Override
    public Void handleDeleteCompany(long id) {
        Optional<Company> comOptional = this.companyRepository.findById(id);
        this.companyRepository.deleteById(id);
        return null;
    }

    @Override
    public Optional<Company> findById(long id) {
        return this.companyRepository.findById(id);
    }
}
