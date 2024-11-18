package com.userms.service.Interface;

import com.userms.DTO.RoleDTO;
import com.userms.DTO.RoleDefaultPermissionDTO;
import com.userms.entity.RoleDefaultPermissionEntity;
import com.userms.entity.RoleEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface RoleServiceInterface {

    boolean isRoleIdExists(Long roleId);

    boolean isRoleExists(String role);

    RoleDTO createRole(RoleDTO roleDTO);

    RoleDTO updateRole(Long roleId, RoleDTO roleDTO);

    RoleDTO getRoleByUserId(Long userId);

    RoleDTO deleteRoleById(Long roleId);

    List<RoleDTO> getAllRoles();

    RoleDTO getRoleByRoleId(Long roleId);

    RoleDTO entityToRoleDTO(RoleEntity roleEntity);


    RoleDefaultPermissionDTO createRoleDefaultPermission(RoleDefaultPermissionDTO roleDefaultPermissionDTO);

    RoleDefaultPermissionDTO updateRoleDefaultPermission(Long id, RoleDefaultPermissionDTO roleDefaultPermissionDTO);

    RoleDefaultPermissionDTO getByRoleDefaultPermissionId(Long id);

    boolean deleteRoleDefaultPermissionById(Long roleDefaultPermissionId);

        RoleDefaultPermissionDTO entityToRoleDefaultPermissionDto(RoleDefaultPermissionEntity roleDefaultPermissionEntity);

}
