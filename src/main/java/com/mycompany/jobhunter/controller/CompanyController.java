package com.mycompany.jobhunter.controller;

import com.mycompany.jobhunter.domain.dto.response.ResultPaginationDTO;
import com.mycompany.jobhunter.domain.entity.Company;
import com.mycompany.jobhunter.service.contract.ICompanyService;
import com.mycompany.jobhunter.util.annotation.ApiMessage;

import com.mycompany.jobhunter.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.persistence.Id;
import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final ICompanyService companyService;

    public CompanyController(ICompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    @ApiMessage("Create company api")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company reqCompany) {

        return ResponseEntity.status(HttpStatus.CREATED).body(this.companyService.handleCreateCompany(reqCompany));
    }

    @GetMapping("/companies")
    @ApiMessage("Fetch all companies")
    public ResponseEntity<ResultPaginationDTO> getCompany(
            @Filter Specification<Company> spec, Pageable pageable) {

        return ResponseEntity.ok(this.companyService.handleGetCompany(spec, pageable));
    }

    @PutMapping("/companies")
    @ApiMessage("Update a company")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company reqCompany) {
        Company updatedCompany = this.companyService.handleUpdateCompany(reqCompany);
        if(updatedCompany != null) {
            return ResponseEntity.ok(updatedCompany);
        }
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
    }

    @DeleteMapping("/companies/{id}")
    @ApiMessage("Delete a company")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") long id) {
        this.companyService.handleDeleteCompany(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/companies/{id}")
    @ApiMessage("fetch company by id")
    public ResponseEntity<Company> fetchCompanyById(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Company> cOptional = this.companyService.findById(id);
        if(cOptional.isPresent()) {
            return ResponseEntity.ok().body(cOptional.get());
        }
        throw new IdInvalidException("id not found");
    }
}
