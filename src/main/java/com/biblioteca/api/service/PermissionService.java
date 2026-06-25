package com.biblioteca.api.service;

import com.biblioteca.api.dto.PermissionRequest;
import com.biblioteca.api.dto.PermissionResponse;
import com.biblioteca.api.model.Permission;
import com.biblioteca.api.model.Role;
import com.biblioteca.api.repository.PermissionRepository;
import com.biblioteca.api.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    public PermissionService(PermissionRepository permissionRepository, RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public List<PermissionResponse> getPermissions(Long roleId) {
        List<Permission> permissions;
        if (roleId != null) {
            Optional<Role> role = roleRepository.findById(roleId);
            permissions = role.map(permissionRepository::findByRole).orElse(Collections.emptyList());
        } else {
            permissions = permissionRepository.findAll();
        }
        return permissions.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<PermissionResponse> getPermission(Long id) {
        return permissionRepository.findById(id).map(this::mapToResponse);
    }

    @Transactional
    public PermissionResponse createPermission(PermissionRequest request) {
        Permission permission = new Permission();
        permission.setName(request.getName());
        if (request.getRoleId() != null) {
            roleRepository.findById(request.getRoleId()).ifPresent(permission::setRole);
        }
        permission = permissionRepository.save(permission);
        return mapToResponse(permission);
    }

    @Transactional
    public PermissionResponse updatePermission(Long id, PermissionRequest request) {
        Permission permission = permissionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Permiso no encontrado"));
        permission.setName(request.getName());
        if (request.getRoleId() != null) {
            roleRepository.findById(request.getRoleId()).ifPresent(permission::setRole);
        } else {
            permission.setRole(null);
        }
        permission = permissionRepository.save(permission);
        return mapToResponse(permission);
    }

    @Transactional
    public void deletePermission(Long id) {
        Permission permission = permissionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Permiso no encontrado"));
        permissionRepository.delete(permission);
    }

    private PermissionResponse mapToResponse(Permission permission) {
        PermissionResponse response = new PermissionResponse();
        response.setId(permission.getId());
        response.setName(permission.getName());
        if (permission.getRole() != null) {
            response.setRoleId(permission.getRole().getId());
            response.setRoleName(permission.getRole().getName());
        }
        return response;
    }
}
