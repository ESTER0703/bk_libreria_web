package com.biblioteca.api.repository;

import com.biblioteca.api.model.Permission;
import com.biblioteca.api.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    List<Permission> findByRole(Role role);
    void deleteByRole(Role role);
}
