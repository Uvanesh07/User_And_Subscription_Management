package com.userms.controller;

import com.userms.DTO.ResponseBO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.userms.DTO.RoleDTO;
import com.userms.DTO.RoleDefaultPermissionDTO;
import com.userms.DTO.StatusConstant;
import com.userms.service.Interface.RoleServiceInterface;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

@RestController
@RequestMapping("/v1/api/role")
@Api(value = "Role Related operations", tags = "Role Management")
public class RoleController {

    private final Logger LOGGER = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    RoleServiceInterface roleServiceInterface;

    @PostMapping
    @ApiOperation(value = "Create Role", notes = "This endpoint allows admin to create a role.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Role Created Successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 409, message = "Role Name Already Exists"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO createRole(@RequestBody RoleDTO roleDTO) {
        try {
            if (roleServiceInterface.isRoleExists(roleDTO.getRole())) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_CONFLICT, HttpStatus.CONFLICT.getReasonPhrase(),null,roleDTO.getRole()+" "+ StatusConstant.EXISTS);
            }

            LOGGER.info("Entering createRole Successfully");
            RoleDTO createdRole = roleServiceInterface.createRole(roleDTO);
            LOGGER.info("Exiting createRole Successfully");
            return new ResponseBO(org.apache.http.HttpStatus.SC_CREATED, HttpStatus.CREATED.getReasonPhrase(), createdRole, StatusConstant.CREATED);
        } catch (Exception ex) {
            LOGGER.error("An error occurred while processing the request", ex);
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),null,StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{roleId}")
    @ApiOperation(value = "Update role details", notes = "This endpoint allows updating role details.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Role details updated successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "Role not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO updateRole(@RequestBody RoleDTO roleDTO, @PathVariable Long roleId) {
        try {
            String name = roleDTO.getRole();
            LOGGER.info("Entering the updatePermission method");

            RoleDTO updatedRole = roleServiceInterface.updateRole(roleId, roleDTO);

            if (updatedRole == null) {
                LOGGER.error("Role ID not found");
                return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(),null,"Role Id" +" "+roleId+" "+ StatusConstant.NOT_FOUND);
            }else if (Objects.equals(updatedRole.getRole(), "true")) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_CONFLICT,HttpStatus.CONFLICT.getReasonPhrase(),null,name+" "+ StatusConstant.EXISTS);
            }

            LOGGER.info("Role details updated successfully");
            return new ResponseBO(org.apache.http.HttpStatus.SC_OK, HttpStatus.OK.getReasonPhrase(), updatedRole, StatusConstant.UPDATED);
        } catch (Exception ex) {
            LOGGER.error("An error occurred while processing the request", ex);
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),null, StatusConstant.INTERNAL_SERVER_ERROR);
        } finally {
            LOGGER.info("Exiting the updatePermission method");
        }
    }



    @GetMapping("/{roleId}")
    @ApiOperation(value = "Get role by ID", notes = "Get role information by its ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved role"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "Role not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO getRoleById(@PathVariable Long roleId) {
        try {
            LOGGER.info("Entering getRoleById method in RoleController");
            RoleDTO role = roleServiceInterface.getRoleByRoleId(roleId);
            if (role != null) {
                LOGGER.info("Exiting getRoleById method successfully in RoleController");
                return new ResponseBO(org.apache.http.HttpStatus.SC_OK, HttpStatus.OK.getReasonPhrase(), role, StatusConstant.GET);
            } else {
                LOGGER.warn("Role not found for ID: {} in getRoleById", roleId);
                return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(),null,"Role Id" +" "+roleId+" "+ StatusConstant.NOT_FOUND);
            }
        } catch (Exception ex) {
            LOGGER.error("An error occurred while processing the request", ex);
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),null,StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }



    @GetMapping("/getAll")
    @ApiOperation(value = "Get all roles", notes = "Get a list of all roles.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of roles"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 204, message = "Roles Not Found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO getAllRoles() {
        try {
            LOGGER.info("Entering getAllRoles method");
            List<RoleDTO> roles = roleServiceInterface.getAllRoles();
            LOGGER.info("Exiting getAllRoles method successfully");
            if(!roles.isEmpty()){
                return new ResponseBO(org.apache.http.HttpStatus.SC_OK, HttpStatus.OK.getReasonPhrase(), roles, roles.size()+ " " + StatusConstant.GET_LIST);
            } else {
                return new ResponseBO(org.apache.http.HttpStatus.SC_NO_CONTENT,HttpStatus.NO_CONTENT.getReasonPhrase(),null,StatusConstant.NO_CONTENT);
            }
        } catch (Exception ex) {
            LOGGER.error("An error occurred while processing the request", ex);
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),null,StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/user/{userId}")
    @ApiOperation(value = "Get Role details By User Id", notes = "This endpoint allows to get a role details by user id.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Get a role by user ID Successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "Role not found for user ID"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO getRoleByUserId(@PathVariable Long userId) {
        try {
            LOGGER.info("Entering getRoleByUserId method");
            RoleDTO roleDTO = roleServiceInterface.getRoleByUserId(userId);
            LOGGER.info("Exiting getRoleByUserId method successfully");
            return new ResponseBO(org.apache.http.HttpStatus.SC_OK, HttpStatus.OK.getReasonPhrase(), roleDTO, StatusConstant.GET);
        } catch (IllegalArgumentException ex) {
            LOGGER.warn("Role not found for user ID: {}", userId);
            return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(),null,"Role" +" "+ StatusConstant.NOT_FOUND);
        } catch (Exception ex) {
            LOGGER.error("An error occurred while processing the request", ex);
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),null,StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{roleId}")
    @ApiOperation(value = "Delete Role By Id", notes = "This endpoint allows admin to delete a role by its ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Role Deleted Successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "Role Id Not Found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO deleteRoleById(@PathVariable Long roleId) {
        try {
            LOGGER.info("Entering deleteRoleById method");
            RoleDTO delete = roleServiceInterface.deleteRoleById(roleId);
            if(Objects.equals(delete.getRole(), "true")){
                return new ResponseBO(org.apache.http.HttpStatus.SC_CONFLICT,HttpStatus.CONFLICT.getReasonPhrase(),null,"Role" +" "+ StatusConstant.MERGE);
            }
            LOGGER.info("Existing deleteRoleById method");
            return new ResponseBO(org.apache.http.HttpStatus.SC_OK, HttpStatus.OK.getReasonPhrase(), null, StatusConstant.DELETED);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Role ID {} not found", roleId);
            return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(),null,"Role Id" +" "+roleId+" "+ StatusConstant.NOT_FOUND);
        } catch (Exception ex) {
            LOGGER.error("Error deleting role ID {}: {}", roleId, ex.getMessage());
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),null,StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/defaultPermission")
    @ApiOperation(value = "Create Role Default Permission", notes = "This endpoint allows admin to create a role default permission.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Role Default Permission Created Successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO createRoleDefaultPermission(@RequestBody RoleDefaultPermissionDTO roleDefaultPermissionDTO) {
        try {
            LOGGER.info("Entering createRoleDefaultPermission method successfully");
            RoleDefaultPermissionDTO createdRoleDefaultPermission = roleServiceInterface.createRoleDefaultPermission(roleDefaultPermissionDTO);

            if (createdRoleDefaultPermission == null) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(),null,"Role" +" "+ StatusConstant.NOT_FOUND);
            }

            LOGGER.info("Exiting createRoleDefaultPermission method successfully");
            return new ResponseBO(org.apache.http.HttpStatus.SC_CREATED, HttpStatus.CREATED.getReasonPhrase(), createdRoleDefaultPermission, StatusConstant.CREATED);
        } catch (Exception ex) {
            LOGGER.error("An error occurred while processing the request", ex);
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),null,StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/defaultPermission/{roleDefaultPermissionId}")
    @ApiOperation(value = "Update the default permission", notes = "This endpoint allows admin to update the defaultPermission of a role.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Default permission updated successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "RoleDefaultPermission ID not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO updateRoleDefaultPermission(@PathVariable Long roleDefaultPermissionId, @RequestBody RoleDefaultPermissionDTO roleDefaultPermissionDTO) {
        try {
            LOGGER.info("Updating role default permission with ID: {}", roleDefaultPermissionId);

            roleDefaultPermissionDTO.setId(roleDefaultPermissionId);
            RoleDefaultPermissionDTO updatedRoleDefaultPermission = roleServiceInterface.updateRoleDefaultPermission(roleDefaultPermissionId, roleDefaultPermissionDTO);

            if (updatedRoleDefaultPermission == null) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(),null,"RoleDefaultPermission Id" +" "+roleDefaultPermissionId+" "+ StatusConstant.NOT_FOUND);
            } else {
                LOGGER.info("Exiting updateRoleDefaultPermission method successfully");
                return new ResponseBO(org.apache.http.HttpStatus.SC_OK, HttpStatus.OK.getReasonPhrase(), updatedRoleDefaultPermission, StatusConstant.UPDATED);
            }
        } catch (Exception ex) {
            LOGGER.error("An error occurred while processing the request", ex);
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),null,StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/defaultPermission/{roleDefaultPermissionId}")
    @ApiOperation(value = "Get role by ID", notes = "Get role_default_permission information by its ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved role"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "Role not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO getByRoleDefaultPermissionId(@PathVariable Long roleDefaultPermissionId) {
        try {
            LOGGER.info("Entering getRoleById method");
            RoleDefaultPermissionDTO role = roleServiceInterface.getByRoleDefaultPermissionId(roleDefaultPermissionId);
            if (role != null) {
                LOGGER.info("Exiting getRoleById method successfully");
                return new ResponseBO(org.apache.http.HttpStatus.SC_OK, HttpStatus.OK.getReasonPhrase(), role, StatusConstant.GET);
            } else {
                LOGGER.warn("Role not found for ID: {}", roleDefaultPermissionId);
                return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(),null,"RoleDefaultPermission Id" +" "+roleDefaultPermissionId+" "+ StatusConstant.NOT_FOUND);
            }
        } catch (Exception ex) {
            LOGGER.error("An error occurred while processing the request", ex);
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),null,StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/defaultPermission/file")
    @ApiOperation(value = "Get default permission file", notes = "This endpoint retrieves default permission from a file.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Default Permission retrieved successfully"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO getDefaultPermission() {
        try {
            LOGGER.info("Entering getDefaultPermission method");
            Resource resource = new ClassPathResource("json/default.json");
            InputStream inputStream = resource.getInputStream();
            String jsonContent = readFromInputStream(inputStream);
            inputStream.close();
            ObjectMapper objectMapper = new ObjectMapper();
            Object jsonDataAsObject = objectMapper.readValue(jsonContent, Object.class);

            LOGGER.info("Exiting getDefaultPermission method successfully");
            return new ResponseBO(org.apache.http.HttpStatus.SC_OK, HttpStatus.OK.getReasonPhrase(), jsonDataAsObject, StatusConstant.GET);

        } catch (Exception ex) {
            LOGGER.error("An error occurred while processing the request", ex);
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),null,StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }

    private String readFromInputStream(InputStream inputStream) {
        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
            return scanner.useDelimiter("\\A").next();
        }
    }


    @DeleteMapping("/defaultPermission/{roleDefaultPermissionId}")
    @ApiOperation(value = "Delete Role Default Permission By Id", notes = "This endpoint allows admin to delete role default permission by id with the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Role Default Permission Deleted Successfully"),
            @ApiResponse(code = 404, message = "Role Default Permission Id Not Found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO deleteRoleDefaultPermissionById(@PathVariable Long roleDefaultPermissionId) {
        LOGGER.info("Deleting role default permission with ID: {}", roleDefaultPermissionId);

        try {
            boolean delete = roleServiceInterface.deleteRoleDefaultPermissionById(roleDefaultPermissionId);
            if(delete){
                return new ResponseBO(org.apache.http.HttpStatus.SC_CONFLICT,HttpStatus.CONFLICT.getReasonPhrase(),null,"Default Permission" +" "+ StatusConstant.MERGE);
            }
            LOGGER.info("Deleted role default permission with ID: {}", roleDefaultPermissionId);
            return new ResponseBO(org.apache.http.HttpStatus.SC_OK, HttpStatus.OK.getReasonPhrase(), null, StatusConstant.DELETED);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Role Default Permission ID {} not found", roleDefaultPermissionId);
            return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(),null,"RoleDefaultPermission ID" +" "+roleDefaultPermissionId+" "+ StatusConstant.NOT_FOUND);
        } catch (Exception ex) {
            LOGGER.error("An error occurred while processing the request", ex);
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),null,StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }



}