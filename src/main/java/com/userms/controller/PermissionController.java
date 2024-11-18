package com.userms.controller;

import com.userms.DTO.PermissionDTO;
import com.userms.DTO.ResponseBO;
import com.userms.DTO.StatusConstant;
import com.userms.service.Interface.PermissionServiceInterface;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/api/permission")
@Api(value = "Permission Related operations", tags = "Permission Management")
public class PermissionController {

    private final Logger LOGGER = LoggerFactory.getLogger(PermissionController.class);

    @Autowired
    PermissionServiceInterface permissionServiceInterface;

    @PostMapping
    @ApiOperation(value = "Create Permission", notes = "This endpoint allows admin to create a permission.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Permission Created Successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "Role or User Not Found"),
            @ApiResponse(code = 409, message = "Permission Name Already Exists"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO createPermission(@RequestBody PermissionDTO permissionDTO) {
        try {
            if (permissionServiceInterface.isPermissionNameExists(permissionDTO.getPermissionName())) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_CONFLICT,HttpStatus.CONFLICT.getReasonPhrase(),null,permissionDTO.getPermissionName() +" "+ StatusConstant.EXISTS);
            }
            else if (!permissionServiceInterface.isUserIdExists(permissionDTO.getUserId())) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(),null,"User"+" "+StatusConstant.NOT_FOUND);
            }

            LOGGER.info("Entering createPermission Successfully");
            PermissionDTO createdPermission = permissionServiceInterface.createPermission(permissionDTO);
            LOGGER.info("Exiting createPermission Successfully");
            return new ResponseBO(org.apache.http.HttpStatus.SC_CREATED, HttpStatus.CREATED.getReasonPhrase(), createdPermission, StatusConstant.CREATED);
        } catch (Exception ex) {
            LOGGER.error("An error occurred while processing the request", ex);
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),null,StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/{permissionId}")
    @ApiOperation(value = "Update Permission", notes = "This endpoint allows to update a permission.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Permission updated successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "Permission ID Not Found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO updatePermission(@RequestBody PermissionDTO permissionDTO, @PathVariable Long permissionId) {
        try {
            LOGGER.info("Updating permission with ID: {}", permissionDTO.getPermissionId());

            String name = permissionDTO.getPermissionName();

            PermissionDTO updatedPermission = permissionServiceInterface.updatePermission(permissionDTO, permissionId);

            if (updatedPermission == null) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(),null, "Permission Id" +" "+ StatusConstant.NOT_FOUND);
            } else if (Objects.equals(updatedPermission.getPermissionName(), "true")) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_CONFLICT,HttpStatus.CONFLICT.getReasonPhrase(),null,name+" "+ StatusConstant.EXISTS);
            }
            return new ResponseBO(org.apache.http.HttpStatus.SC_OK, HttpStatus.OK.getReasonPhrase(), updatedPermission, StatusConstant.UPDATED);
        } catch (Exception ex) {
            LOGGER.error("An error occurred while processing the request", ex);
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),null,StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{permissionId}")
    @ApiOperation(value = "Get Permission details By Permission Id", notes = "This endpoint allows admin to get permission details by id with the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Get a permission by ID Successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "PermissionId Not Found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO getPermissionById(@PathVariable Long permissionId) {
        if (permissionServiceInterface.isPermissionIdExists(permissionId)) {
            try {
                LOGGER.info("Entering to the [getPermissionById] successfully");
                PermissionDTO permission = permissionServiceInterface.getByPermissionId(permissionId);
                LOGGER.info("Exiting to the [getPermissionById] successfully");
                return new ResponseBO(org.apache.http.HttpStatus.SC_OK, HttpStatus.OK.getReasonPhrase(), permission, StatusConstant.GET);
            } catch (Exception ex) {
                LOGGER.error("An error occurred while processing the request", ex);
                return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),null,StatusConstant.INTERNAL_SERVER_ERROR);
            }
        } else {
            LOGGER.warn("Permission ID {} not found in getPermissionById", permissionId);
            return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(),null,"Permission Id"+" "+StatusConstant.NOT_FOUND);

        }
    }

    @GetMapping("/getAll")
    @ApiOperation(value = "Get all permission details", notes = "This endpoint allows admin to get all permission details with the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Get All Permissions successfully"),
            @ApiResponse(code = 204, message = "Permissions Not Found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO getAllPermissions() {
        try {
            LOGGER.info("Entering GetAllPermissions Successfully");
            List<PermissionDTO> permissionDTOList = permissionServiceInterface.getAllPermissions();
            LOGGER.info("Exiting GetAllPermissions Successfully");

            if (!permissionDTOList.isEmpty()) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_OK, HttpStatus.OK.getReasonPhrase(), permissionDTOList, permissionDTOList.size()+ " "+ StatusConstant.GET_LIST);
            } else {
                return new ResponseBO(org.apache.http.HttpStatus.SC_NO_CONTENT,HttpStatus.NO_CONTENT.getReasonPhrase(),null, StatusConstant.NO_CONTENT);
            }
        } catch (Exception ex) {
            LOGGER.error("An error occurred while processing the request", ex);
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),null,StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/user/{userId}")
    @ApiOperation(value = "Get Permission details By User Id", notes = "This endpoint allows to get a permission details by user id.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Get a permission by user ID Successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "Permission not found for user ID"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO getByUserId(@PathVariable("userId") Long userId) {
        try {
            LOGGER.info("Entering getByUserId Successfully");
            PermissionDTO permissionDTO = permissionServiceInterface.getPermissionByUserId(userId);
            LOGGER.info("Existing getByUserId successfully");
            return new ResponseBO(org.apache.http.HttpStatus.SC_OK, HttpStatus.OK.getReasonPhrase(), permissionDTO, StatusConstant.GET);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("User ID {} not found", userId);
            return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(),null,"Permission"+" "+StatusConstant.NOT_FOUND);
        } catch (Exception ex) {
            LOGGER.error("An error occurred while processing the request", ex);
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),null,StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{permissionId}")
    @ApiOperation(value = "Delete Permission", notes = "This endpoint allows users to delete their account.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "User deleted successfully"),
            @ApiResponse(code = 400, message = "Invalid user ID"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO deletePermission(@PathVariable Long permissionId) {
        try {
            LOGGER.info("Entering deletePermission method");
            PermissionDTO delete = permissionServiceInterface.deletePermissionById(permissionId);
            if(Objects.equals(delete.getPermissionName(), "true")){
                return new ResponseBO(org.apache.http.HttpStatus.SC_CONFLICT,HttpStatus.CONFLICT.getReasonPhrase(),null,"Permission"+" "+StatusConstant.MERGE);
            }
            LOGGER.info("Exiting deletePermission method successfully");
            return new ResponseBO(org.apache.http.HttpStatus.SC_OK, HttpStatus.OK.getReasonPhrase() ,null,StatusConstant.DELETED);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Permission ID {} not found", permissionId);
            return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(),null,"Permission Id"+" "+StatusConstant.NOT_FOUND);
        }catch (Exception ex) {
            LOGGER.error("An error occurred while processing the request", ex);
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),null,StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }

}