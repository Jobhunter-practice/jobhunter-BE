package com.mycompany.jobhunter.config;

import java.util.List;

import com.mycompany.jobhunter.util.error.PermissionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.mycompany.jobhunter.domain.entity.Permission;
import com.mycompany.jobhunter.domain.entity.Role;
import com.mycompany.jobhunter.domain.entity.User;
import com.mycompany.jobhunter.service.contract.IUserService;
import com.mycompany.jobhunter.util.SecurityUtil;

public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    IUserService userService;

    @Override
    @Transactional
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {

        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path= " + path);
        System.out.println(">>> httpMethod= " + httpMethod);
        System.out.println(">>> requestURI= " + requestURI);

        // check permission
        String email = SecurityUtil.getCurrentUser().isPresent() == true
                ? SecurityUtil.getCurrentUser().get()
                : "";
        if (email != null && !email.isEmpty()) {
            User user = this.userService.handleGetUserByEmail(email);
            if (user != null) {
                Role role = user.getRole();
                if (role != null) {
                    List<Permission> permissions = role.getPermissions();
                    boolean isAllow = permissions.stream().anyMatch(item -> item.getApiPath().equals(path)
                            && item.getMethod().equals(httpMethod));

                    if (isAllow == false) {
                        throw new PermissionException("You don't have permission to access this resource");
                    }
                } else {
                    throw new PermissionException("You don't have permission to access this resource");
                }
            }
        }

        return true;
    }
}
