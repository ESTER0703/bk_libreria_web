package com.biblioteca.api.dto;

import java.util.List;

public class RoleResponse {
    private Long id;
    private String name;
    private List<PermissionResponse> permissions;

    public RoleResponse() {}

    public RoleResponse(Long id, String name, List<PermissionResponse> permissions) {
        this.id = id;
        this.name = name;
        this.permissions = permissions;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<PermissionResponse> getPermissions() { return permissions; }
    public void setPermissions(List<PermissionResponse> permissions) { this.permissions = permissions; }
}
