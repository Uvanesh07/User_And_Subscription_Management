package com.userms.DTO;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RoleDTO {

    private Long roleId;

    @NotBlank(message = "Role name is required")
    @Size(min = 3, max = 50, message = "Role name must be between 3 and 50 characters")
    private String role;

    @NotNull(message = "Description is required")
    @NotEmpty(message = "Description should not be empty")
    private String description;

    private RoleDefaultPermissionDTO defaultPermission;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RoleDefaultPermissionDTO getDefaultPermission() {
        return defaultPermission;
    }

    public void setDefaultPermission(RoleDefaultPermissionDTO defaultPermission) {
        this.defaultPermission = defaultPermission;
    }
}
