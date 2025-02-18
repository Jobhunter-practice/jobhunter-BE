package com.mycompany.jobhunter.service.contract;

import com.mycompany.jobhunter.domain.dto.response.ResultPaginationDTO;
import com.mycompany.jobhunter.domain.entity.Permission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface IPermissionService {
    boolean isPermissionExist(Permission p);

    Permission fetchById(long id);

    Permission create(Permission p);

    Permission update(Permission p);

    void delete(long id);

    ResultPaginationDTO getPermissions(Specification<Permission> spec, Pageable pageable);

    boolean isSameName(Permission p);
}
