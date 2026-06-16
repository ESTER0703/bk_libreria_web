package com.biblioteca.api.dto;

/**
 * DTO para respuestas de usuario que se envían al frontend.
 * Omite la contraseña y agrega el campo numérico `roleId`.
 */
public class UserResponse {

    private Long id;
    private String nombre;
    private String email;
    private String rol;
    private int roleId;
    private String permisos;
    private String estado;

    public UserResponse(Long id, String nombre, String email, String rol, int roleId, String permisos, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
        this.roleId = roleId;
        this.permisos = permisos;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getRol() {
        return rol;
    }

    public int getRoleId() {
        return roleId;
    }

    public String getPermisos() {
        return permisos;
    }

    public String getEstado() {
        return estado;
    }
}
