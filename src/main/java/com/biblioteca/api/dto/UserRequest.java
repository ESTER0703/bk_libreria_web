package com.biblioteca.api.dto;

/**
 * DTO para peticiones de creación/actualización de usuario.
 * Acepta tanto `rol` (string) como `roleId` (numérico) para facilitar
 * la normalización entre frontend y backend.
 */
public class UserRequest {
    private String nombre;
    private String email;
    private String password;
    private String rol;
    private Integer roleId;
    private String permisos;
    private String estado;

    public UserRequest() {}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getPermisos() {
        return permisos;
    }

    public void setPermisos(String permisos) {
        this.permisos = permisos;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
