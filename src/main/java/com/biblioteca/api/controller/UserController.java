package com.biblioteca.api.controller;

import com.biblioteca.api.dto.UserResponse;
import com.biblioteca.api.dto.UserRequest;
import com.biblioteca.api.model.User;
import com.biblioteca.api.model.Role;
import com.biblioteca.api.repository.UserRepository;
import com.biblioteca.api.repository.RoleRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller que expone operaciones CRUD para usuarios.
 * Las respuestas públicas omiten la contraseña e incluyen roleId y roleName.
 */
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Devuelve la lista de usuarios. El objeto devuelto es un DTO seguro
     * que no contiene la contraseña e incluye roleId desde la base de datos.
     */
    @GetMapping
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserResponse(
                        u.getId(),
                        u.getNombre(),
                        u.getEmail(),
                        u.getRol(),
                        u.getRoleId() != null ? u.getRoleId().intValue() : 0,
                        u.getPermisos(),
                        u.getEstado()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Devuelve un usuario por id en formato DTO público (sin password).
     */
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        User u = userRepository.findById(id).orElseThrow();
        return new UserResponse(
                u.getId(),
                u.getNombre(),
                u.getEmail(),
                u.getRol(),
                u.getRoleId() != null ? u.getRoleId().intValue() : 0,
                u.getPermisos(),
                u.getEstado()
        );
    }

    @PutMapping("/{id}")
    public User updateUser(
            @PathVariable Long id,
            @RequestBody UserRequest userData) {
        User user = userRepository.findById(id)
                .orElseThrow();

        if (userData.getNombre() != null) user.setNombre(userData.getNombre());
        if (userData.getEmail() != null) user.setEmail(userData.getEmail());
        if (userData.getPassword() != null && !userData.getPassword().isEmpty()) user.setPassword(userData.getPassword());

        // Actualizar rol usando roleId (preferencia) o rol (fallback)
        if (userData.getRoleId() != null) {
            Role role = roleRepository.findById(userData.getRoleId().longValue()).orElse(null);
            if (role != null) {
                user.setRoleId(role.getId());
                user.setRol(role.getName());
            }
        } else if (userData.getRol() != null) {
            user.setRol(userData.getRol());
        }

        if (userData.getPermisos() != null) user.setPermisos(userData.getPermisos());
        if (userData.getEstado() != null) user.setEstado(userData.getEstado());

        return userRepository.save(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }

    @PostMapping
    public User createUser(@RequestBody UserRequest req) {
        User user = new User();
        user.setNombre(req.getNombre());
        user.setEmail(req.getEmail());
        user.setPassword(req.getPassword());

        // Asignar rol usando roleId (preferencia) o rol (fallback)
        if (req.getRoleId() != null) {
            Role role = roleRepository.findById(req.getRoleId().longValue()).orElse(null);
            if (role != null) {
                user.setRoleId(role.getId());
                user.setRol(role.getName());
            }
        } else {
            user.setRol(req.getRol());
        }

        user.setPermisos(req.getPermisos());
        user.setEstado(req.getEstado());

        return userRepository.save(user);
    }

    // Métodos de mapeo removidos; ahora se usa roleId directamente de la BD.
}
