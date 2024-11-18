package com.userms.entity;

import javax.persistence.*;

@Entity
@Table(name = "role")
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long roleId;

    @Column(name = "role",unique = true,nullable = false)
    private String role;

    private String description;

    @OneToOne(mappedBy = "role")
    private RoleDefaultPermissionEntity defaultPermissions;

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

    public RoleDefaultPermissionEntity getDefaultPermissions() {
        return defaultPermissions;
    }

    public void setDefaultPermissions(RoleDefaultPermissionEntity defaultPermissions) {
        this.defaultPermissions = defaultPermissions;
    }
}
