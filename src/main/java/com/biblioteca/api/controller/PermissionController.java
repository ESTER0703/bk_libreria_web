package com.biblioteca.api.controller;

import com.biblioteca.api.dto.PermissionRequest;
import com.biblioteca.api.dto.PermissionResponse;
import com.biblioteca.api.model.Permission;
import com.biblioteca.api.model.Role;
import com.biblioteca.api.repository.PermissionRepository;
import com.biblioteca.api.repository.RoleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/permissions")
@CrossOrigin(origins = "*")
public class PermissionController {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    public PermissionController(PermissionRepository permissionRepository, RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public List<PermissionResponse> getPermissions(@RequestParam(value = "roleId", required = false) Long roleId) {
        List<Permission> perms;
        if (roleId != null) {
            Optional<Role> r = roleRepository.findById(roleId);
            perms = r.map(permissionRepository::findByRole).orElse(Collections.emptyList());
        } else {
            perms = permissionRepository.findAll();
        }
        return perms.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionResponse> getPermission(@PathVariable Long id) {
        Optional<Permission> opt = permissionRepository.findById(id);
        return opt.map(p -> ResponseEntity.ok(mapToResponse(p))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PermissionResponse> createPermission(@RequestBody PermissionRequest request) {
        Permission p = new Permission();
        p.setName(request.getName());
        if (request.getRoleId() != null) {
            roleRepository.findById(request.getRoleId()).ifPresent(p::setRole);
        }
        p = permissionRepository.save(p);
        return new ResponseEntity<>(mapToResponse(p), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermissionResponse> updatePermission(@PathVariable Long id, @RequestBody PermissionRequest request) {
        Optional<Permission> opt = permissionRepository.findById(id);
        if (!opt.isPresent()) return ResponseEntity.notFound().build();
        Permission p = opt.get();
        p.setName(request.getName());
        if (request.getRoleId() != null) {
            roleRepository.findById(request.getRoleId()).ifPresent(p::setRole);
        } else {
            p.setRole(null);
        }
        p = permissionRepository.save(p);
        return ResponseEntity.ok(mapToResponse(p));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        Optional<Permission> opt = permissionRepository.findById(id);
        if (!opt.isPresent()) return ResponseEntity.notFound().build();
        permissionRepository.delete(opt.get());
        return ResponseEntity.noContent().build();
    }

    private PermissionResponse mapToResponse(Permission p) {
        PermissionResponse res = new PermissionResponse();
        res.setId(p.getId());
        res.setName(p.getName());
        if (p.getRole() != null) {
            res.setRoleId(p.getRole().getId());
            res.setRoleName(p.getRole().getName());
        }
        return res;
    }

}
