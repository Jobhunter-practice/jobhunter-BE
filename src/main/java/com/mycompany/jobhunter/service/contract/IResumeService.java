package com.mycompany.jobhunter.service.contract;

import com.mycompany.jobhunter.domain.dto.response.ResultPaginationDTO;
import com.mycompany.jobhunter.domain.dto.response.resume.ResCreateResumeDTO;
import com.mycompany.jobhunter.domain.dto.response.resume.ResFetchResumeDTO;
import com.mycompany.jobhunter.domain.dto.response.resume.ResUpdateResumeDTO;
import com.mycompany.jobhunter.domain.entity.Resume;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface IResumeService {
    Optional<Resume> fetchById(long id);

    boolean checkResumeExistByUserAndJob(Resume resume);

    ResCreateResumeDTO create(Resume resume);

    ResUpdateResumeDTO update(Resume resume);

    void delete(long id);

    ResFetchResumeDTO getResume(Resume resume);

    ResultPaginationDTO fetchAllResume(Specification<Resume> spec, Pageable pageable);

    ResultPaginationDTO fetchResumeByUser(Pageable pageable);
}
