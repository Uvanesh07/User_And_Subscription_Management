package com.userms.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userms.DTO.PermissionDTO;
import com.userms.entity.PermissionEntity;
import com.userms.entity.UserEntity;
import com.userms.repository.IPermissionRepo;
import com.userms.repository.IUserRepo;
import com.userms.service.Interface.PermissionServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionServiceInterface {

    private final Logger LOGGER = LoggerFactory.getLogger(PermissionServiceImpl.class);

    @Autowired
    IPermissionRepo iPermissionRepo;

    @Autowired
    IUserRepo iUserRepo;

    @Override
    public boolean isPermissionIdExists(Long permissionId) {
        return iPermissionRepo.existsByPermissionId(permissionId);
    }

    @Override
    public boolean isPermissionNameExists(String permissionName){
        return iPermissionRepo.existsByPermissionName(permissionName);
    }

    @Override
    public boolean isUserIdExists(Long userId) {
        return iUserRepo.existsByUserId(userId);
    }

    @Override
    public PermissionDTO createPermission(PermissionDTO permissionDTO) {
        LOGGER.info("Entering into the createPermission method in PermissionServiceImpl");

        PermissionEntity permission = new PermissionEntity();
        permission.setPermissionName(permissionDTO.getPermissionName());
        permission.setPermission(permissionDTO.getPermission().toString());

        UserEntity user = iUserRepo.findById(permissionDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found for ID: " + permissionDTO.getUserId()));

        PermissionEntity permissionObj = iPermissionRepo.findById(user.getPermission().getPermissionId())
                .orElseThrow(() -> new IllegalArgumentException("Permission not found for user ID: " + user.getUserId()));
        permissionObj.setUser(null);
        iPermissionRepo.save(permissionObj);

        user.setPermission(null);
        permission.setUser(user);

        permission = iPermissionRepo.save(permission);

        PermissionEntity savedPermissionEntity = iPermissionRepo.findById(permission.getPermissionId())
                .orElseThrow(() -> new IllegalStateException("Failed to save permission."));

        user.setPermission(savedPermissionEntity);
        iUserRepo.save(user);

        LOGGER.info("Exiting from the createPermission method in PermissionServiceImpl");

        return entityToPermissionDTO(permission);
    }


    @Override
    public PermissionDTO updatePermission(PermissionDTO permissionDTO, Long permissionId) {
        LOGGER.info("Entering into the updatePermission method in PermissionServiceImpl");

        Optional<PermissionEntity> optionalPermission = iPermissionRepo.findById(permissionId);
        if (optionalPermission.isEmpty()) {
            return null;
        }

        PermissionEntity existingPermission = optionalPermission.get();
        if (!existingPermission.getPermissionName().equals(permissionDTO.getPermissionName())) {
            if (isPermissionNameExists(permissionDTO.getPermissionName())) {
                permissionDTO.setPermissionName("true");
                return permissionDTO;
            }
            existingPermission.setPermissionName(permissionDTO.getPermissionName());

        }
        existingPermission.setPermission(permissionDTO.getPermission().toString());
        existingPermission = iPermissionRepo.save(existingPermission);

        LOGGER.info("Exiting from the updatePermission method in PermissionServiceImpl");
        return entityToPermissionDTO(existingPermission);
    }


    @Override
    public PermissionDTO getByPermissionId(Long permissionId) {
        LOGGER.info("Entering into the getByPermissionId method in PermissionServiceImpl");
        Optional<PermissionEntity> permissionOptional = iPermissionRepo.findById(permissionId);
        LOGGER.info("Exiting from the getByPermissionId method in PermissionServiceImpl");
        return permissionOptional.map(this::entityToPermissionDTO).orElse(null);
    }

    @Override
    public List<PermissionDTO> getAllPermissions() {
        LOGGER.info("Entering into the getAllPermissions method in PermissionServiceImpl");
        List<PermissionEntity> permissionEntities = iPermissionRepo.findAll();
        LOGGER.info("Exiting from the getAllPermissions method in PermissionServiceImpl");
        return permissionEntities.stream()
                .map(this::entityToPermissionDTO)
                .collect(Collectors.toList());
    }


    @Override
    public PermissionDTO getPermissionByUserId(Long userId) {
        LOGGER.info("Entering into the getPermissionByRoleIdAndUserId method in PermissionServiceImpl");
        PermissionEntity permission = iPermissionRepo.findByUserUserId(userId);

        if (permission != null) {
            LOGGER.info("Successfully found permission by User ID");
            return entityToPermissionDTO(permission);
        } else {
            LOGGER.info("Exiting from the getPermissionByRoleIdAndUserId method in PermissionServiceImpl");
            throw new IllegalArgumentException("Permission not found for User ID: " + userId);
        }
    }


    @Override
    public PermissionDTO deletePermissionById(Long permissionId) {
        LOGGER.info("Deleting permission with ID: {}", permissionId);

        PermissionEntity permission = iPermissionRepo.findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found with ID: " + permissionId));

        UserEntity users = iUserRepo.findByPermissionPermissionId(permissionId);
        if (users != null) {
            if(Objects.equals(users.getPermission().getPermissionId(), permissionId)){
                PermissionDTO permissionDTO = new PermissionDTO();
                permissionDTO.setPermissionName("true");
                return permissionDTO;
            }
        }
        else {
            iPermissionRepo.delete(permission);
        }
        LOGGER.info("Deleted permission with ID: {}", permissionId);
        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setPermissionName("false");
        return permissionDTO;
    }



    @Override
    public PermissionDTO entityToPermissionDTO(PermissionEntity permissionEntity) {
        LOGGER.info("Entering into the entityToPermissionDTO method in PermissionServiceImpl");
        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setPermissionId(permissionEntity.getPermissionId());
        permissionDTO.setPermissionName(permissionEntity.getPermissionName());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Object jsonDataAsObject = objectMapper.readValue(permissionEntity.getPermission(), Object.class);
            permissionDTO.setPermission(jsonDataAsObject);
        } catch (Exception e) {
            return null;
        }

        if (permissionEntity.getUser() != null) {
            permissionDTO.setUserId(permissionEntity.getUser().getUserId());
        }


        LOGGER.info("Existing into the entityToPermissionDTO method in PermissionServiceImpl");
        return permissionDTO;
    }



}
