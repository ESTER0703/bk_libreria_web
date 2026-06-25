package com.biblioteca.api.service;

import com.biblioteca.api.dto.UserRequest;
import com.biblioteca.api.dto.UserResponse;
import com.biblioteca.api.model.Role;
import com.biblioteca.api.model.User;
import com.biblioteca.api.repository.RoleRepository;
import com.biblioteca.api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return toResponse(user);
    }

    @Transactional
    public User createUser(UserRequest req) {
        User user = new User();
        user.setNombre(req.getNombre());
        user.setEmail(req.getEmail());
        user.setPassword(req.getPassword());
        applyRole(user, req);
        user.setPermisos(req.getPermisos());
        user.setEstado(req.getEstado());
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, UserRequest userData) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (userData.getNombre() != null) user.setNombre(userData.getNombre());
        if (userData.getEmail() != null) user.setEmail(userData.getEmail());
        if (userData.getPassword() != null && !userData.getPassword().isEmpty()) user.setPassword(userData.getPassword());
        applyRole(user, userData);
        if (userData.getPermisos() != null) user.setPermisos(userData.getPermisos());
        if (userData.getEstado() != null) user.setEstado(userData.getEstado());

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        userRepository.delete(user);
    }

    private void applyRole(User user, UserRequest userData) {
        if (userData.getRoleId() != null) {
            Role role = roleRepository.findById(userData.getRoleId().longValue()).orElse(null);
            if (role != null) {
                user.setRoleId(role.getId());
                user.setRol(role.getName());
            }
        } else if (userData.getRol() != null) {
            user.setRol(userData.getRol());
        }
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getNombre(),
                user.getEmail(),
                user.getRol(),
                user.getRoleId() != null ? user.getRoleId().intValue() : 0,
                user.getPermisos(),
                user.getEstado()
        );
    }
}
