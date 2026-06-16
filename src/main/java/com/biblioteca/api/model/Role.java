package com.biblioteca.api.model;

import jakarta.persistence.*;
import java.util.List;

/**
 * Entidad Role: representa un rol del sistema. Un rol puede tener muchos permisos.
 * Este modelo implementa la relación 1 -> N (Role -> Permission).
 */
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Permission> permissions;

    public Role() {}

    public Role(String name) { this.name = name; }

    public Long getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<Permission> getPermissions() { return permissions; }

    public void setPermissions(List<Permission> permissions) { this.permissions = permissions; }
}
