package com.biblioteca.api.service;

import com.biblioteca.api.dto.PermissionResponse;
import com.biblioteca.api.dto.RoleRequest;
import com.biblioteca.api.dto.RoleResponse;
import com.biblioteca.api.model.Permission;
import com.biblioteca.api.model.Role;
import com.biblioteca.api.repository.PermissionRepository;
import com.biblioteca.api.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> getRoles() {
        return roleRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<RoleResponse> getRole(Long id) {
        return roleRepository.findById(id).map(this::mapToResponse);
    }

    @Transactional
    public RoleResponse createRole(RoleRequest request) {
        Role role = new Role();
        role.setName(request.getName());
        role = roleRepository.save(role);

        if (request.getPermissions() != null) {
            List<Permission> perms = new ArrayList<>();
            for (String pName : request.getPermissions()) {
                if (pName == null || pName.trim().isEmpty()) continue;
                Permission permission = new Permission();
                permission.setName(pName.trim());
                permission.setRole(role);
                perms.add(permissionRepository.save(permission));
            }
            role.setPermissions(perms);
        }

        return mapToResponse(role);
    }

    @Transactional
    public RoleResponse updateRole(Long id, RoleRequest request) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
        role.setName(request.getName());

        permissionRepository.deleteByRole(role);
        List<Permission> perms = new ArrayList<>();
        if (request.getPermissions() != null) {
            for (String pName : request.getPermissions()) {
                if (pName == null || pName.trim().isEmpty()) continue;
                Permission permission = new Permission();
                permission.setName(pName.trim());
                permission.setRole(role);
                perms.add(permissionRepository.save(permission));
            }
        }
        role.setPermissions(perms);
        role = roleRepository.save(role);
        return mapToResponse(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
        permissionRepository.deleteByRole(role);
        roleRepository.delete(role);
    }

    private RoleResponse mapToResponse(Role role) {
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setName(role.getName());
        if (role.getPermissions() != null) {
            List<PermissionResponse> permissions = role.getPermissions().stream().map(permission -> {
                PermissionResponse permissionResponse = new PermissionResponse();
                permissionResponse.setId(permission.getId());
                permissionResponse.setName(permission.getName());
                if (permission.getRole() != null) {
                    permissionResponse.setRoleId(permission.getRole().getId());
                    permissionResponse.setRoleName(permission.getRole().getName());
                }
                return permissionResponse;
            }).collect(Collectors.toList());
            response.setPermissions(permissions);
        }
        return response;
    }
}
