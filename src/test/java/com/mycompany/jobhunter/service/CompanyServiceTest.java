package com.mycompany.jobhunter.service;

import com.mycompany.jobhunter.domain.dto.response.ResultPaginationDTO;
import com.mycompany.jobhunter.domain.entity.Company;
import com.mycompany.jobhunter.domain.entity.Job;
import com.mycompany.jobhunter.repository.CompanyRepository;
import com.mycompany.jobhunter.repository.UserRepository;
import com.mycompany.jobhunter.service.implement.CompanyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CompanyServiceTest {
    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CompanyServiceImpl companyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleCreateCompany_ShouldSaveAndReturnCompany() {
        Company company = new Company();
        company.setId(1L);
        company.setName("Tech Corp");
        company.setDescription("Tech Corp Description");
        company.setAddress("Tech Corp Address");
        company.setLogo("Tech Corp Logo");
        List<Job> jobs = new ArrayList<>();
        jobs.add(new Job());
        company.setJobs(jobs);

        when(companyRepository.save(any(Company.class))).thenReturn(company);

        Company savedCompany = companyService.handleCreateCompany(company);

        assertNotNull(savedCompany);
        assertEquals(1L, savedCompany.getId());
        assertEquals("Tech Corp", savedCompany.getName());
        assertEquals("Tech Corp Description", savedCompany.getDescription());
        assertEquals("Tech Corp Address", savedCompany.getAddress());
        assertEquals("Tech Corp Logo", savedCompany.getLogo());
        assertEquals(jobs, savedCompany.getJobs());
        verify(companyRepository, times(1)).save(company);
    }

    @Test
    void handleGetCompany_ShouldReturnPaginatedResult() {
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Company> spec = mock(Specification.class);

        Company company = new Company();
        company.setId(1L);
        Page<Company> companyPage = new PageImpl<>(Collections.singletonList(company), pageable, 1);

        when(companyRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(companyPage);

        ResultPaginationDTO result = companyService.handleGetCompany(spec, pageable);

        assertNotNull(result);
        assertEquals(1, result.getMeta().getTotal());
        verify(companyRepository, times(1)).findAll(spec, pageable);
    }

    @Test
    void handleUpdateCompany_ShouldUpdateAndReturnCompany() {
        Company existingCompany = new Company();
        existingCompany.setId(1L);
        existingCompany.setName("Old Name");

        Company updatedCompany = new Company();
        updatedCompany.setId(1L);
        updatedCompany.setName("New Name");

        when(companyRepository.findById(1L)).thenReturn(Optional.of(existingCompany));
        when(companyRepository.save(any(Company.class))).thenReturn(updatedCompany);

        Company result = companyService.handleUpdateCompany(updatedCompany);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        verify(companyRepository, times(1)).findById(1L);
        verify(companyRepository, times(1)).save(existingCompany);
    }

    @Test
    void handleUpdateCompany_ShouldReturnNull_WhenCompanyNotFound() {
        Company updatedCompany = new Company();
        updatedCompany.setId(1L);

        when(companyRepository.findById(1L)).thenReturn(Optional.empty());

        Company result = companyService.handleUpdateCompany(updatedCompany);

        assertNull(result);
        verify(companyRepository, times(1)).findById(1L);
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void handleDeleteCompany_ShouldDeleteCompany() {
        Company company = new Company();
        company.setId(1L);

        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

        companyService.handleDeleteCompany(1L);

        verify(companyRepository, times(1)).deleteById(1L);
    }

    @Test
    void findById_ShouldReturnCompany_WhenExists() {
        Company company = new Company();
        company.setId(1L);

        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

        Optional<Company> result = companyService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(companyRepository, times(1)).findById(1L);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        when(companyRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Company> result = companyService.findById(1L);

        assertTrue(result.isEmpty());
        verify(companyRepository, times(1)).findById(1L);
    }
}
