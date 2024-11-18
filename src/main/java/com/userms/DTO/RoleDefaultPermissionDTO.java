package com.userms.DTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Data
@Component
public class RoleDefaultPermissionDTO {
    private Long id;

    @NotNull(message = "Role ID is required")
    private Long roleId;

    @NotNull(message = "Permission is required")
    private Object permission;

    public RoleDefaultPermissionDTO() {
        try {
            ClassPathResource resource = new ClassPathResource("json/default.json");
            InputStream inputStream = resource.getInputStream();
            byte[] bytes = inputStream.readAllBytes();
            this.permission = new String(bytes);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object getPermission() {
        return permission;
    }
    public void convertPermissionToObject() {
        try {
            this.permission = new ObjectMapper().readValue(this.permission.toString(), Object.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPermission(Object permission) {
        this.permission = permission;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
