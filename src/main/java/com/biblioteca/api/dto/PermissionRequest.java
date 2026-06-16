package com.biblioteca.api.dto;

public class PermissionRequest {
    private String name;
    private Long roleId;

    public PermissionRequest() {}

    public PermissionRequest(String name, Long roleId) {
        this.name = name;
        this.roleId = roleId;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
}
