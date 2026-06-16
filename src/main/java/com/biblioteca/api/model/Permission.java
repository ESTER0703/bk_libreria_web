package com.biblioteca.api.model;

import jakarta.persistence.*;

/**
 * Entidad Permission: permisos asociados a un rol (1 role -> N permissions).
 */
@Entity
@Table(name = "permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    public Permission() {}

    public Permission(String name, Role role) {
        this.name = name;
        this.role = role;
    }

    public Long getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Role getRole() { return role; }

    public void setRole(Role role) { this.role = role; }
}
