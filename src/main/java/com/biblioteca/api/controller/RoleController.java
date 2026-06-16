package com.biblioteca.api.controller;

import com.biblioteca.api.dto.PermissionResponse;
import com.biblioteca.api.dto.RoleRequest;
import com.biblioteca.api.dto.RoleResponse;
import com.biblioteca.api.model.Permission;
import com.biblioteca.api.model.Role;
import com.biblioteca.api.repository.PermissionRepository;
import com.biblioteca.api.repository.RoleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
public class RoleController {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleController(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @GetMapping
    public List<RoleResponse> getRoles() {
        return roleRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getRole(@PathVariable Long id) {
        Optional<Role> opt = roleRepository.findById(id);
        return opt.map(role -> ResponseEntity.ok(mapToResponse(role))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RoleResponse> createRole(@RequestBody RoleRequest request) {
        Role role = new Role();
        role.setName(request.getName());
        role = roleRepository.save(role);

        if (request.getPermissions() != null) {
            List<Permission> perms = new ArrayList<>();
            for (String pName : request.getPermissions()) {
                if (pName == null || pName.trim().isEmpty()) continue;
                Permission p = new Permission();
                p.setName(pName.trim());
                p.setRole(role);
                perms.add(permissionRepository.save(p));
            }
            role.setPermissions(perms);
        }

        return new ResponseEntity<>(mapToResponse(role), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> updateRole(@PathVariable Long id, @RequestBody RoleRequest request) {
        Optional<Role> opt = roleRepository.findById(id);
        if (!opt.isPresent()) return ResponseEntity.notFound().build();
        Role role = opt.get();
        role.setName(request.getName());

        // Reemplazar permisos: borrar los actuales y crear los nuevos
        permissionRepository.deleteByRole(role);
        List<Permission> perms = new ArrayList<>();
        if (request.getPermissions() != null) {
            for (String pName : request.getPermissions()) {
                if (pName == null || pName.trim().isEmpty()) continue;
                Permission p = new Permission();
                p.setName(pName.trim());
                p.setRole(role);
                perms.add(permissionRepository.save(p));
            }
        }
        role.setPermissions(perms);
        role = roleRepository.save(role);
        return ResponseEntity.ok(mapToResponse(role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        Optional<Role> opt = roleRepository.findById(id);
        if (!opt.isPresent()) return ResponseEntity.notFound().build();
        Role role = opt.get();
        permissionRepository.deleteByRole(role);
        roleRepository.delete(role);
        return ResponseEntity.noContent().build();
    }

    private RoleResponse mapToResponse(Role role) {
        RoleResponse res = new RoleResponse();
        res.setId(role.getId());
        res.setName(role.getName());
        if (role.getPermissions() != null) {
            List<PermissionResponse> prs = role.getPermissions().stream().map(p -> {
                PermissionResponse pr = new PermissionResponse();
                pr.setId(p.getId());
                pr.setName(p.getName());
                if (p.getRole() != null) {
                    pr.setRoleId(p.getRole().getId());
                    pr.setRoleName(p.getRole().getName());
                }
                return pr;
            }).collect(Collectors.toList());
            res.setPermissions(prs);
        }
        return res;
    }

}
