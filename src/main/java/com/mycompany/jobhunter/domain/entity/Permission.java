package com.mycompany.jobhunter.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.mycompany.jobhunter.util.SecurityUtil;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Permission's name is required")
    private String name;

    @NotBlank(message = "apiPath is required")
    private String apiPath;

    @NotBlank(message = "method is required")
    private String method;

    @NotBlank(message = "module is required")
    private String module;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    public Permission(String name, String apiPath, String method, String module) {
        this.name = name;
        this.apiPath = apiPath;
        this.method = method;
        this.module = module;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "permissions")
    @JsonIgnore
    private List<Role> roles;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUser().isPresent() == true
                ? SecurityUtil.getCurrentUser().get()
                : "";

        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedBy = SecurityUtil.getCurrentUser().isPresent() == true
                ? SecurityUtil.getCurrentUser().get()
                : "";

        this.updatedAt = Instant.now();
    }
}
