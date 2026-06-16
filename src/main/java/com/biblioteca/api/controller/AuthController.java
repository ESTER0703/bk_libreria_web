package com.biblioteca.api.controller;

import com.biblioteca.api.dto.LoginRequest;
import com.biblioteca.api.model.User;
import com.biblioteca.api.repository.UserRepository;
import com.biblioteca.api.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Optional<User> opt = Optional.empty();

        if (req.getUsername() != null) {
            opt = userRepository.findByEmail(req.getUsername());
            if (opt.isEmpty()) opt = userRepository.findByNombre(req.getUsername());
            if (opt.isEmpty()) {
                try {
                    Long id = Long.valueOf(req.getUsername());
                    opt = userRepository.findById(id);
                } catch (Exception ignored) {
                }
            }
        }

        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Credenciales inválidas"));
        }

        User u = opt.get();
        if (u.getPassword() == null || !u.getPassword().equals(req.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Credenciales inválidas"));
        }

        String token = jwtUtil.generateToken(u);
        long roleId = u.getRoleId() != null ? u.getRoleId() : 0L;

        Map<String, Object> userSafe = Map.of(
                "id", u.getId(),
                "nombre", u.getNombre(),
                "email", u.getEmail(),
                "rol", u.getRol(),
                "roleId", roleId,
                "permisos", u.getPermisos(),
                "estado", u.getEstado()
        );

        return ResponseEntity.ok(Map.of("token", token, "user", userSafe));
    }

    // Mapeo removido; roleId viene directamente de users.role_id
}
