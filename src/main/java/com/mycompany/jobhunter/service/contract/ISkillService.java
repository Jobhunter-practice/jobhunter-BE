package com.mycompany.jobhunter.service.contract;

import com.mycompany.jobhunter.domain.dto.response.ResultPaginationDTO;
import com.mycompany.jobhunter.domain.entity.Skill;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ISkillService {
    boolean isNameExist(String name);

    Skill fetchSkillById(long id);

    Skill createSkill(Skill s);

    Skill updateSkill(Skill s);

    void deleteSkill(long id);

    ResultPaginationDTO fetchAllSkills(Specification<Skill> spec, Pageable pageable);
}
