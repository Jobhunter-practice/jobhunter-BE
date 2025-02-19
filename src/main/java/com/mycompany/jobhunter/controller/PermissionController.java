package com.mycompany.jobhunter.controller;

import com.mycompany.jobhunter.domain.dto.response.ResultPaginationDTO;
import com.mycompany.jobhunter.domain.entity.Permission;
import com.mycompany.jobhunter.service.contract.IPermissionService;
import com.mycompany.jobhunter.util.annotation.ApiMessage;
import com.mycompany.jobhunter.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {
    private final IPermissionService permissionService;

    public PermissionController(IPermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create permission")
    public ResponseEntity<Permission> create(@Valid @RequestBody Permission p) throws IdInvalidException {
        // check exist
        if (this.permissionService.isPermissionExist(p)) {
            throw new IdInvalidException("Permission has already been created.");
        }

        // create new permission
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.create(p));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update permission")
    public ResponseEntity<Permission> update(@Valid @RequestBody Permission p) throws IdInvalidException {
        // check exist by id
        if (this.permissionService.fetchById(p.getId()) == null) {
            throw new IdInvalidException("Permission  id = " + p.getId() + " not found.");
        }

        // check exist by module, apiPath and method
        if (this.permissionService.isPermissionExist(p)) {
            // check name
            if (this.permissionService.isSameName(p)) {
                throw new IdInvalidException("Permission has already existed.");
            }
        }

        // update permission
        return ResponseEntity.ok().body(this.permissionService.update(p));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete permission")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        // check exist by id
        if (this.permissionService.fetchById(id) == null) {
            throw new IdInvalidException("Permission id = " + id + " not found.");
        }
        this.permissionService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/permissions")
    @ApiMessage("Get all permissions")
    public ResponseEntity<ResultPaginationDTO> getPermissions(
            @Filter Specification<Permission> spec, Pageable pageable) {

        return ResponseEntity.ok(this.permissionService.getPermissions(spec, pageable));
    }
}
