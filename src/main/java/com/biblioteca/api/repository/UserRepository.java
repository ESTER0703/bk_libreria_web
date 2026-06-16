package com.biblioteca.api.repository;

import com.biblioteca.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
	Optional<User> findByNombre(String nombre);

	@Query("SELECT DISTINCT u.rol FROM User u")
	java.util.List<String> findDistinctRoles();

	@Query("SELECT DISTINCT u.permisos FROM User u")
	java.util.List<String> findDistinctPermisos();
}

/*
 * Nota: los métodos findDistinctRoles() y findDistinctPermisos() se usan por
 * la capa de presentación para poblar selects en el frontend sin exponer
 * lógica adicional en los controladores.
 */
