package com.biblioteca.api.service;

import com.biblioteca.api.dto.LoginRequest;
import com.biblioteca.api.model.User;
import com.biblioteca.api.repository.UserRepository;
import com.biblioteca.api.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> login(LoginRequest req) {
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

        User user = opt.get();
        if (user.getPassword() == null || !user.getPassword().equals(req.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Credenciales inválidas"));
        }

        String token = jwtUtil.generateToken(user);
        long roleId = user.getRoleId() != null ? user.getRoleId() : 0L;

        HashMap<String, Object> userSafe = new HashMap<>();
        userSafe.put("id", user.getId());
        userSafe.put("nombre", user.getNombre());
        userSafe.put("email", user.getEmail());
        userSafe.put("rol", user.getRol());
        userSafe.put("roleId", roleId);
        userSafe.put("permisos", user.getPermisos());
        userSafe.put("estado", user.getEstado());

        return ResponseEntity.ok(Map.of("token", token, "user", userSafe));
    }
}
