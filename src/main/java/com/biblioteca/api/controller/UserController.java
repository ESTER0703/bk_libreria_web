package com.biblioteca.api.controller;

import com.biblioteca.api.model.User;
import com.biblioteca.api.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
	return userRepository.findById(id)
        	.orElseThrow();
    }

    @PutMapping("/{id}")
    public User updateUser(
      @PathVariable Long id,
      @RequestBody User userData) {
      User user = userRepository.findById(id)
            .orElseThrow();
	user.setNombre(userData.getNombre());
	user.setEmail(userData.getEmail());
	user.setPassword(userData.getPassword());
	user.setRol(userData.getRol());
	user.setPermisos(userData.getPermisos());
	user.setEstado(userData.getEstado());

	return userRepository.save(user);
      }

	@DeleteMapping("/{id}")
	public void deleteUser(@PathVariable Long id) {
	    userRepository.deleteById(id);
	}
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }
}
