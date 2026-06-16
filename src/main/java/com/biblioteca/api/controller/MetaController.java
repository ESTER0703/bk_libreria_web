package com.biblioteca.api.controller;

import com.biblioteca.api.repository.UserRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints para obtener metadatos del sistema relacionados con usuarios,
 * por ejemplo listas de roles y permisos presentes en la base de datos.
 */
@RestController
@RequestMapping("/meta")
@CrossOrigin(origins = "*")
public class MetaController {

    private final UserRepository userRepository;

    public MetaController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Devuelve la lista de roles distintos actualmente almacenados en la tabla users.
     */
    @GetMapping("/roles")
    public List<String> getRoles() {
        return userRepository.findDistinctRoles();
    }

    /**
     * Devuelve la lista de permisos distintos actualmente almacenados en la tabla users.
     */
    @GetMapping("/permisos")
    public List<String> getPermisos() {
        return userRepository.findDistinctPermisos();
    }
}
