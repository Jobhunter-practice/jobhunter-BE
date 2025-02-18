package com.mycompany.jobhunter.service;

import com.mycompany.jobhunter.domain.dto.response.ResultPaginationDTO;
import com.mycompany.jobhunter.domain.dto.response.job.ResCreateJobDTO;
import com.mycompany.jobhunter.domain.dto.response.job.ResUpdateJobDTO;
import com.mycompany.jobhunter.domain.entity.Job;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface IJobService {
    ResCreateJobDTO create(Job j);

    ResUpdateJobDTO update(Job j, Job jobInDB);

    void delete(long id);

    ResultPaginationDTO fetchAll(Specification<Job> spec, Pageable pageable);

    Optional<Job> fetchJobById(long id);
}
