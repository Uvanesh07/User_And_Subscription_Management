package com.userms.service.Interface;

import com.userms.DTO.PermissionDTO;
import com.userms.entity.PermissionEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PermissionServiceInterface {

        boolean isPermissionIdExists(Long permissionId);

        boolean isUserIdExists(Long userId);

        boolean isPermissionNameExists(String permissionName);

    PermissionDTO createPermission(PermissionDTO permissionDTO);

    PermissionDTO updatePermission(PermissionDTO permissionDTO, Long permissionId);

    PermissionDTO getByPermissionId(Long permissionId);

    List<PermissionDTO> getAllPermissions();

    PermissionDTO getPermissionByUserId(Long userId);

    PermissionDTO deletePermissionById(Long permissionId);

        PermissionDTO entityToPermissionDTO(PermissionEntity permissionEntity);

}
