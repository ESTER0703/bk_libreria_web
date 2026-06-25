package com.biblioteca.api.service;

import com.biblioteca.api.dto.RoleRequest;
import com.biblioteca.api.dto.RoleResponse;
import com.biblioteca.api.model.Permission;
import com.biblioteca.api.model.Role;
import com.biblioteca.api.repository.PermissionRepository;
import com.biblioteca.api.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    void createRoleSavesRoleAndPermissions() {
        RoleRequest request = new RoleRequest();
        request.setName("ADMIN");
        request.setPermissions(List.of("READ", "WRITE"));

        Role savedRole = new Role();
        savedRole.setName("ADMIN");

        when(roleRepository.save(any(Role.class))).thenReturn(savedRole);
        when(permissionRepository.save(any(Permission.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RoleResponse response = roleService.createRole(request);

        assertEquals("ADMIN", response.getName());
        assertEquals(2, response.getPermissions().size());
        verify(roleRepository).save(any(Role.class));
        verify(permissionRepository, times(2)).save(any(Permission.class));
    }
}
