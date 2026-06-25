package com.biblioteca.api.controller;

import com.biblioteca.api.dto.UserResponse;
import com.biblioteca.api.dto.UserRequest;
import com.biblioteca.api.model.User;
import com.biblioteca.api.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller que expone operaciones CRUD para usuarios.
 * Las respuestas públicas omiten la contraseña e incluyen roleId y roleName.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Devuelve la lista de usuarios. El objeto devuelto es un DTO seguro
     * que no contiene la contraseña e incluye roleId desde la base de datos.
     */
    @GetMapping
    public List<UserResponse> getUsers() {
        return userService.getUsers();
    }

    /**
     * Devuelve un usuario por id en formato DTO público (sin password).
     */
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public User updateUser(
            @PathVariable Long id,
            @RequestBody UserRequest userData) {
        return userService.updateUser(id, userData);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PostMapping
    public User createUser(@RequestBody UserRequest req) {
        return userService.createUser(req);
    }
}
