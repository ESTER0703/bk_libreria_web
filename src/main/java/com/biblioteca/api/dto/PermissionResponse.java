package com.biblioteca.api.dto;

public class PermissionResponse {
    private Long id;
    private String name;
    private Long roleId;
    private String roleName;

    public PermissionResponse() {}

    public PermissionResponse(Long id, String name, Long roleId, String roleName) {
        this.id = id;
        this.name = name;
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
}
