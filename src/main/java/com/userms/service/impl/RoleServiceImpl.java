package com.userms.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userms.DTO.RoleDTO;
import com.userms.DTO.RoleDefaultPermissionDTO;
import com.userms.entity.PermissionEntity;
import com.userms.entity.RoleDefaultPermissionEntity;
import com.userms.entity.RoleEntity;
import com.userms.entity.UserEntity;
import com.userms.repository.IRoleDefaultPermissionRepo;
import com.userms.repository.IRoleRepo;
import com.userms.repository.IUserRepo;
import com.userms.service.Interface.RoleServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleServiceInterface {

    private final Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    IRoleRepo iRoleRepo;

    @Autowired
    IUserRepo iUserRepo;

    @Autowired
    IRoleDefaultPermissionRepo iRoleDefaultPermissionRepo;

    @Value("${superAdmin.role}")
    private String superAdminRole;

    @Override
    public boolean isRoleIdExists(Long roleId) {
        return iRoleRepo.existsByRoleId(roleId);
    }

    @Override
    public boolean isRoleExists(String role) {
        return iRoleRepo.existsByRole(role);
    }


    @Override
    @Transactional
    public RoleDTO createRole(RoleDTO roleDto) {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRole(roleDto.getRole());
        roleEntity.setDescription(roleDto.getDescription());
        roleEntity = iRoleRepo.save(roleEntity);

        if(!Objects.equals(roleEntity.getRole(), superAdminRole)) {
            RoleEntity role = iRoleRepo.findById(roleEntity.getRoleId()).orElse(null);

            RoleDefaultPermissionEntity defaultPermission = new RoleDefaultPermissionEntity();
            RoleDefaultPermissionDTO roleDefaultPermissionDTO = new RoleDefaultPermissionDTO();
            defaultPermission.setPermission(roleDefaultPermissionDTO.getPermission().toString());
            defaultPermission.setRole(role);

            defaultPermission = iRoleDefaultPermissionRepo.save(defaultPermission);

            assert role != null;
            role.setDefaultPermissions(defaultPermission);
            roleEntity = iRoleRepo.save(role);
        }

        return entityToRoleDTO(roleEntity);
    }

    @Override
    public RoleDTO updateRole(Long roleId, RoleDTO roleDTO) {
        RoleEntity existingRoleEntity = iRoleRepo.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        if (!existingRoleEntity.getRole().equals(roleDTO.getRole())) {
            if (isRoleExists(roleDTO.getRole())) {
                roleDTO.setRole("true");
                return roleDTO;
            }
            existingRoleEntity.setRole(roleDTO.getRole());

        }
        existingRoleEntity.setDescription(roleDTO.getDescription());
        existingRoleEntity = iRoleRepo.save(existingRoleEntity);

        LOGGER.info("Existing into the updateRole method in RoleServiceImpl");
        return entityToRoleDTO(existingRoleEntity);

    }

    @Override
    public RoleDTO getRoleByRoleId(Long roleId) {
        LOGGER.info("Entering into the getRoleByRoleId method in RoleServiceImpl");
        Optional<RoleEntity> roleEntityOptional = iRoleRepo.findById(roleId);
        if (roleEntityOptional.isPresent()) {
            LOGGER.info("Existing into the getRoleByRoleId method in RoleServiceImpl");
            return entityToRoleDTO(roleEntityOptional.get());
        } else {
            return null;
        }
    }

    @Override
    public RoleDTO getRoleByUserId(Long userId) {
        LOGGER.info("Entering into the getRoleByUserId method in RoleServiceImpl");

        Optional<UserEntity> userOptional = iUserRepo.findById(userId);
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            RoleEntity role = user.getRole();
            RoleDTO roleDTO = entityToRoleDTO(role);
            LOGGER.info("Successfully found role for user with ID: ");
            return roleDTO;
        } else {
            LOGGER.error("User not found for ID: ");
            throw new IllegalArgumentException("User not found for ID: " + userId);
        }
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        LOGGER.info("Entering into the getAllRoles method in RoleServiceImpl");
        LOGGER.info("Existing into the getAllRoles method in RoleServiceImpl");
        return iRoleRepo.findAll().stream()
                .map(this::entityToRoleDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RoleDTO deleteRoleById(Long roleId) {
        LOGGER.info("Deleting role with ID: {}", roleId);

        RoleEntity role = iRoleRepo.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with ID: " + roleId));

        List<UserEntity> users = iUserRepo.findByRoleRoleId(roleId);

        if (users != null && !users.isEmpty()) {
            for (UserEntity user : users) {
                if (Objects.equals(user.getRole().getRoleId(), roleId)) {
                    RoleDTO roleDTO = new RoleDTO();
                    roleDTO.setRole("true");
                    return roleDTO;
                }
            }
        } else {
            RoleDefaultPermissionEntity defaultPermission = role.getDefaultPermissions();
            if (defaultPermission != null) {
                role.setDefaultPermissions(null);
                iRoleDefaultPermissionRepo.delete(defaultPermission);
            }

            iRoleRepo.delete(role);
        }

        LOGGER.info("Deleted role with ID: {}", roleId);
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setRole("false");
        return roleDTO;
    }


    public RoleDTO entityToRoleDTO(RoleEntity roleEntity) {
        LOGGER.info("Entering into the entityToRoleDTO method in RoleServiceImpl");
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setRoleId(roleEntity.getRoleId());
        roleDTO.setRole(roleEntity.getRole());
        roleDTO.setDescription(roleEntity.getDescription());

        RoleDefaultPermissionEntity defaultPermissionEntity = roleEntity.getDefaultPermissions();
        if (defaultPermissionEntity != null) {
            RoleDefaultPermissionDTO defaultPermissionDto = new RoleDefaultPermissionDTO();
            defaultPermissionDto.setId(defaultPermissionEntity.getId());
            defaultPermissionDto.setRoleId(defaultPermissionEntity.getRole().getRoleId());

            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Object jsonDataAsObject = objectMapper.readValue(defaultPermissionEntity.getPermission(), Object.class);
                defaultPermissionDto.setPermission(jsonDataAsObject);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            roleDTO.setDefaultPermission(defaultPermissionDto);

        }
        LOGGER.info("Exiting from the entityToRoleDTO method in RoleServiceImpl");
        return roleDTO;
    }




    @Override
    public RoleDefaultPermissionDTO createRoleDefaultPermission(RoleDefaultPermissionDTO roleDefaultPermissionDTO) {
        LOGGER.info("Entering into the createRoleDefaultPermission method");

        RoleDefaultPermissionEntity defaultPermission = new RoleDefaultPermissionEntity();
        defaultPermission.setPermission(roleDefaultPermissionDTO.getPermission().toString());
        RoleEntity role = iRoleRepo.findById(roleDefaultPermissionDTO.getRoleId()).orElse(null);

        assert role != null;
        RoleDefaultPermissionEntity roleDefaultPermissionEntity = iRoleDefaultPermissionRepo.findById(role.getDefaultPermissions().getId()).orElse(null);
        assert roleDefaultPermissionEntity != null;
        roleDefaultPermissionEntity.setRole(null);
        iRoleDefaultPermissionRepo.save(roleDefaultPermissionEntity);

        role.setDefaultPermissions(null);
        defaultPermission.setRole(role);

        defaultPermission = iRoleDefaultPermissionRepo.save(defaultPermission);

        RoleDefaultPermissionEntity roleDefault = iRoleDefaultPermissionRepo.findById(defaultPermission.getId()).orElse(null);

        role.setDefaultPermissions(roleDefault);
        iRoleRepo.save(role);

        List<UserEntity> usersWithRole = iUserRepo.findByRoleRoleId(role.getRoleId());
        if(usersWithRole != null){
        for (UserEntity user : usersWithRole) {
            PermissionEntity permissionEntity = user.getPermission();
            permissionEntity.setPermission(defaultPermission.getPermission());
            user.setPermission(permissionEntity);
            iUserRepo.save(user);
        }
        }

        LOGGER.info("Exiting from the createRoleDefaultPermission method");

        return entityToRoleDefaultPermissionDto(defaultPermission);
    }


    @Override
    public RoleDefaultPermissionDTO updateRoleDefaultPermission(Long id, RoleDefaultPermissionDTO roleDefaultPermissionDTO) {
        LOGGER.info("Entering into the updateRoleDefaultPermission method in RoleServiceImpl");
        if (id == null || !iRoleDefaultPermissionRepo.existsById(id)) {
            return null;
        }

        Optional<RoleDefaultPermissionEntity> optionalRoleDefaultPermission = iRoleDefaultPermissionRepo.findById(id);
        if (optionalRoleDefaultPermission.isEmpty()) {
            return null;
        }

        RoleDefaultPermissionEntity existingRoleDefaultPermissionEntity = optionalRoleDefaultPermission.get();
        existingRoleDefaultPermissionEntity.setPermission(roleDefaultPermissionDTO.getPermission().toString());

        RoleDefaultPermissionEntity updatedRoleDefaultPermissionEntity = iRoleDefaultPermissionRepo.save(existingRoleDefaultPermissionEntity);

        List<UserEntity> usersWithRole = iUserRepo.findByRoleRoleId(updatedRoleDefaultPermissionEntity.getRole().getRoleId());
        if(usersWithRole != null){
        for (UserEntity user : usersWithRole) {
            PermissionEntity permissionEntity = user.getPermission();
            permissionEntity.setPermission(updatedRoleDefaultPermissionEntity.getPermission());
            user.setPermission(permissionEntity);
            iUserRepo.save(user);
        }}
        LOGGER.info("Existing into the updateRoleDefaultPermission method in RoleServiceImpl");
        return entityToRoleDefaultPermissionDto(updatedRoleDefaultPermissionEntity);
    }

    @Override
    public RoleDefaultPermissionDTO getByRoleDefaultPermissionId(Long id) {
        LOGGER.info("Entering into the getByRoleDefaultPermissionId method in RoleServiceImpl");
        if (id == null) {
            return null;
        }
        Optional<RoleDefaultPermissionEntity> optionalRoleDefaultPermissionEntity = iRoleDefaultPermissionRepo.findById(id);
        if (optionalRoleDefaultPermissionEntity.isEmpty()) {
            return null;
        }
        LOGGER.info("Existing into the getByRoleDefaultPermissionId method in RoleServiceImpl");
        return entityToRoleDefaultPermissionDto(optionalRoleDefaultPermissionEntity.get());
    }


    @Override
    public boolean deleteRoleDefaultPermissionById(Long roleDefaultPermissionId) {
        LOGGER.info("Deleting role default permission with ID: {}", roleDefaultPermissionId);

        RoleDefaultPermissionEntity roleDefaultPermissionEntity = iRoleDefaultPermissionRepo.findById(roleDefaultPermissionId)
                .orElseThrow(() -> new IllegalArgumentException("Role Default Permission not found with ID: " + roleDefaultPermissionId));

        if (roleDefaultPermissionEntity.getRole() != null) {
            List<UserEntity> users = iUserRepo.findByRoleRoleId(roleDefaultPermissionEntity.getRole().getRoleId());

            if (!users.isEmpty()) {
                for (UserEntity user : users) {
                    if (Objects.equals(user.getRole().getRoleId(), roleDefaultPermissionEntity.getRole().getRoleId())) {
                        return true;
                    }
                }
            } else {
                roleDefaultPermissionEntity.setRole(null);
                iRoleDefaultPermissionRepo.delete(roleDefaultPermissionEntity);
            }
        } else {
            iRoleDefaultPermissionRepo.delete(roleDefaultPermissionEntity);
        }

        LOGGER.info("Deleted role default permission with ID: {}", roleDefaultPermissionId);
        return false;
    }


    @Override
    public RoleDefaultPermissionDTO entityToRoleDefaultPermissionDto(RoleDefaultPermissionEntity roleDefaultPermissionEntity) {
        LOGGER.info("Entering into the entityToRoleDefaultPermissionDto method");
        RoleDefaultPermissionDTO roleDefaultPermissionDTO = new RoleDefaultPermissionDTO();
        roleDefaultPermissionDTO.setId(roleDefaultPermissionEntity.getId());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Object jsonDataAsObject = objectMapper.readValue(roleDefaultPermissionEntity.getPermission(), Object.class);
            roleDefaultPermissionDTO.setPermission(jsonDataAsObject);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (roleDefaultPermissionEntity.getRole() != null) {
            roleDefaultPermissionDTO.setRoleId(roleDefaultPermissionEntity.getRole().getRoleId());
        }
        LOGGER.info("Exiting from the entityToRoleDefaultPermissionDto method");
        return roleDefaultPermissionDTO;
    }
}
