package com.userms.controller;

import com.userms.DTO.*;
import com.userms.entity.CustomUserDetails;
import com.userms.security.jwt.JwtResponse;
import com.userms.security.jwt.JwtUtil;
import com.userms.service.EmailService;
import com.userms.service.Interface.ILoginServiceInterface;
import com.userms.service.Interface.RoleServiceInterface;
import com.userms.service.Interface.SubscriptionInterface;
import com.userms.service.Interface.UserServiceInterface;
import com.userms.service.UserService;
import com.userms.service.impl.RegistrationServiceImpl;
import io.swagger.annotations.*;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("/v1/api/user")
@Api(value = "User Related operations", tags = "User Management")
public class UserController {

    private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserServiceInterface userServiceInterface;

    @Autowired
    ILoginServiceInterface iLoginServiceInterface;

    @Autowired
    RegistrationServiceImpl registrationService;

    @Autowired
    RoleServiceInterface roleServiceInterface;

    @Autowired
    SubscriptionInterface subscriptionInterface;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Value("${role.default.name}")
    private String defaultRoleName;

    @Value("${superAdmin.email}")
    private String superAdminEmail;

    @Value("${superAdmin.password}")
    private String superAdminPassword;

    @PostMapping("/register")
    @ApiOperation(value = "Register a new user", notes = "This endpoint allows users to register with the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "User registered successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 409, message = "Details already exist"),
            @ApiResponse(code = 404, message = "Role ID or Subscription ID not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO registration(@RequestBody UserDTO userDTO) {
        try {
            LOGGER.info("Entering [registration]");

            if (userServiceInterface.isEmailIdExists(userDTO.getEmailId())) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_CONFLICT, HttpStatus.CONFLICT.getReasonPhrase(), null, userDTO.getEmailId() + " " + StatusConstant.EXISTS);
            } else if (userServiceInterface.isMobileNoExists(userDTO.getMobileNo())) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_CONFLICT, HttpStatus.CONFLICT.getReasonPhrase(), null, userDTO.getMobileNo() + " " + StatusConstant.EXISTS);
            } else if (userServiceInterface.isGstinExists(userDTO.getOrganization().getGstin())) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_CONFLICT, HttpStatus.CONFLICT.getReasonPhrase(), null, userDTO.getOrganization().getGstin() + " " + StatusConstant.EXISTS);
            } else if (userServiceInterface.isPanExists(userDTO.getOrganization().getPan())) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_CONFLICT, HttpStatus.CONFLICT.getReasonPhrase(), null, userDTO.getOrganization().getPan() + " " + StatusConstant.EXISTS);
            } else if (userServiceInterface.isTanExists(userDTO.getOrganization().getTan())) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_CONFLICT, HttpStatus.CONFLICT.getReasonPhrase(), null, userDTO.getOrganization().getTan() + " " + StatusConstant.EXISTS);
            } else if (userServiceInterface.isCinExists(userDTO.getOrganization().getCin())) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_CONFLICT, HttpStatus.CONFLICT.getReasonPhrase(), null, userDTO.getOrganization().getCin() + " " + StatusConstant.EXISTS);
            } else if (!Objects.equals(userDTO.getEmailId(), superAdminEmail)) {
                if (!subscriptionInterface.isSubscriptionIdExists(userDTO.getSubscriptionId())) {
                    return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase(), null, "Subscription Id " + userDTO.getSubscriptionId() + " " + StatusConstant.NOT_FOUND);
                } else if (!roleServiceInterface.isRoleExists(defaultRoleName)) {
                    return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase(), null, "Role " + defaultRoleName + " " + StatusConstant.NOT_FOUND);
                }
            }

            UserDTO createdUser = userServiceInterface.createuser(userDTO);
            if (createdUser != null) {
                String generatedPassword = userServiceInterface.generateStrongPassword();
                // emailService.sendWelcomeEmail(createdUser.getEmailId(), createdUser.getEmailId(), generatedPassword);
                LoginDTO login = new LoginDTO();
                login.setUsername(createdUser.getEmailId());
                login.setPassword(Objects.equals(createdUser.getEmailId(), superAdminEmail) ? superAdminPassword : generatedPassword);
                login.setAccountActive(true);
                iLoginServiceInterface.createLogin(login);
            }

            LOGGER.info("Exiting [registration]");
            return new ResponseBO(org.apache.http.HttpStatus.SC_CREATED, HttpStatus.CREATED.getReasonPhrase(), createdUser, StatusConstant.CREATED);

        } catch (Exception ex) {
            LOGGER.error("Unexpected error during registration: {}", ex.getMessage());
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }





    @PutMapping("/{userId}")
    @ApiOperation(value = "Update user details", notes = "This endpoint allows users to update their details.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User details updated successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO updateUser(@RequestBody UserDTO userDTO, @PathVariable Long userId) {
        try {
            LOGGER.info("Entering the updateUser method");
            UserDTO updatedUser = userServiceInterface.updateUser(userDTO,userId);
            if (updatedUser != null) {
                LOGGER.info("User details updated successfully");
                return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), updatedUser, StatusConstant.UPDATED);
            } else {
                LOGGER.error("User not found for ID: {}", userDTO.getUserId());
                return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase(), null, "User Id" +" "+ userDTO.getUserId() +" "+ StatusConstant.NOT_FOUND);
            }
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Role ID or Subscription ID not found");
            return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase(), null, "Role Id:" + userDTO.getRoleId() +" or Subscription Id:" +userDTO.getSubscriptionId() +" "+ StatusConstant.NOT_FOUND);
        } catch (Exception ex) {
            LOGGER.error("An error occurred while processing the request", ex);
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
        } finally {
            LOGGER.info("Exiting the updateUser method");
        }
    }

    @GetMapping("/{userId}")
    @ApiOperation(value = "Get user by ID", notes = "Get permission information by its ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved permission"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "Permission not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO getUserById(@PathVariable Long userId) {
        try {
            LOGGER.info("Entering the getUserById method");

            if (!userServiceInterface.isUserIdExists(userId)) {
                LOGGER.warn("User ID {} not found in getUserById", userId);
                return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase(), null, "User Id" +" "+ userId +" "+ StatusConstant.NOT_FOUND);
            }

            UserDTO user = userServiceInterface.getUserByUserId(userId);

            LOGGER.info("Exiting the getUserById method successfully");
            return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), user, StatusConstant.GET);
        } catch (Exception ex) {
            LOGGER.error("An error occurred while processing the request", ex);
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAll")
    @ApiOperation(value = "Get all users", notes = "Get all users information from the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved all users"),
            @ApiResponse(code = 204, message = "Users not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public PageableResponse<?> getAllUsers(
            @RequestParam int size, @RequestParam int page,
            @RequestParam(required = false) String searchKey
    ) {
        try {
            LOGGER.info("Fetching all users");

            PageableResponse<List<UserDTO>> userDTOs = userServiceInterface.getAllUsers(page, size, searchKey);

            if (userDTOs.getData() == null || !(userDTOs.getData() instanceof List) || ((List<?>) userDTOs.getData()).isEmpty()) {
                LOGGER.warn("No users found");
                return new PageableResponse<>(org.apache.http.HttpStatus.SC_NO_CONTENT, HttpStatus.NO_CONTENT.getReasonPhrase(), 0, 0, null, StatusConstant.NO_CONTENT, 0, 0);
            }

            userDTOs.setCode(org.apache.http.HttpStatus.SC_OK);
            userDTOs.setStatus(HttpStatus.OK.getReasonPhrase());
            userDTOs.setMsg(userDTOs.getSize() + " " + StatusConstant.GET_LIST);
            return userDTOs;
        } catch (Exception ex) {
            LOGGER.error("Error fetching all users: {}", ex.getMessage(), ex);
            return new PageableResponse<>(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), 0, 0, null, StatusConstant.INTERNAL_SERVER_ERROR, 0, 0);
        }
    }



    @GetMapping("/username/{username}")
    @ApiOperation(value = "Get all Values for user", notes = "This endpoint allows to get the User's All Details with the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User Details Get successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "Invalid Username in the system"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO getUserByUsername(@PathVariable String username) {
        try {
            LOGGER.info("GetUserByName Entering Successfully");
            UserDTO userDetails = registrationService.findByUsername(username);
            LOGGER.info("GetUserByName Exiting Successfully");

            if (userDetails == null) {
                LOGGER.error("Username Not Found");
                return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase(), null, username+" "+StatusConstant.NOT_FOUND);
            }
            return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), userDetails, StatusConstant.GET);
        }   catch (Exception ex) {
            LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/role/{roleId}")
    @ApiOperation(value = "Get Users details By Role Id", notes = "This endpoint allows to get user details by role id.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Get users by role ID successfully"),
            @ApiResponse(code = 204, message = "No Users found"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "Role not found for role ID"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO getUsersByRoleId(@PathVariable Long roleId) {
        try {
            LOGGER.info("Entering getUsersByRoleId method successfully");

            if (!roleServiceInterface.isRoleIdExists(roleId)){
                return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase(), null, "Role ID" +" "+ roleId +" "+ StatusConstant.NOT_FOUND);
            }

            List<UserDTO> user = userServiceInterface.getUsersByRoleId(roleId);

            LOGGER.info("Exiting getUsersByRoleId method successfully");
            if (user.isEmpty()) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_NO_CONTENT, HttpStatus.NO_CONTENT.getReasonPhrase(), null, StatusConstant.NO_CONTENT);

            }
            return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), user, user.size() + " " + StatusConstant.GET_LIST);
        } catch (Exception ex) {
            LOGGER.error("An error occurred while processing the request", ex);
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{userId}")
    @ApiOperation(value = "Delete User", notes = "This endpoint allows users to delete their account.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "User deleted successfully"),
            @ApiResponse(code = 400, message = "Invalid user ID"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO deleteUser(@PathVariable Long userId) {
        try {
            LOGGER.info("Entering deleteUser method successfully");
            userServiceInterface.deleteUser(userId);
            LOGGER.info("Existing deleteUser method successfully");
            return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), null, StatusConstant.DELETED);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("User ID {} not found", userId);
            return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase(), null, "User Id" +" "+ userId +" "+ StatusConstant.NOT_FOUND);
        } catch (Exception ex) {
            LOGGER.error("An error occurred while deleting user with ID: {}", userId, ex);
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/login")
    @ApiOperation(value = "Login User", notes = "This endpoint allows users to login to the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Login successful"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 401, message = "Username or password is incorrect"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public JwtResponse login(@RequestBody LoginDTO loginDTO) {
        try {
            if (!iLoginServiceInterface.isUsernameExists(loginDTO.getUsername())) {
                LOGGER.warn("Username {} does not exist", loginDTO.getUsername());
                return new JwtResponse(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), null, null, "Incorrect Username");
//                return new ResponseBO(org.apache.http.HttpStatus.SC_UNAUTHORIZED,HttpStatus.UNAUTHORIZED.getReasonPhrase(), JwtResponse(null,null), "Incorrect Username");
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
            );

            if (!iLoginServiceInterface.updateLoginTime(loginDTO)) {
                LOGGER.warn("Failed to update login time for username {}", loginDTO.getUsername());
                return new JwtResponse(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), null, null, "Login not successful");
//                return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), JwtResponse(null,null), "Login not successful");
            }

            UserDetails userDetails = userService.loadUserByUsername(loginDTO.getUsername());
            String token = jwtUtil.generateToken(userDetails.getUsername());

            LOGGER.info("User {} logged in successfully", loginDTO.getUsername());
            return new JwtResponse(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), token, loginDTO.getUsername(), "Login Successfully");
//            return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), JwtResponse(token, loginDTO.getUsername()), "Login Successfully");
        } catch (BadCredentialsException e) {
            return new JwtResponse(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), null, null, "Incorrect Password");
//            return new ResponseBO(org.apache.http.HttpStatus.SC_UNAUTHORIZED,HttpStatus.UNAUTHORIZED.getReasonPhrase(), JwtResponse(null, null), "Incorrect Password");
        } catch (Exception ex) {
            LOGGER.error("An error occurred while processing the request", ex);
            return new JwtResponse(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, null, StatusConstant.INTERNAL_SERVER_ERROR);
//            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), JwtResponse(null, null), StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/profile/{username}")
    @ApiOperation(value = "Get user details", notes = "Retrieve user details based on the provided JWT token.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved user details"),
            @ApiResponse(code = 401, message = "Invalid or expired token"),
            @ApiResponse(code = 404, message = "User not found or not subscribed"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO getUserDetails(@PathVariable String username, @RequestHeader("Authorization") String token) {
        try {

            if (!registrationService.isEmailIdExists(username)) {
                LOGGER.error("Username Not Found ");
                return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase(), null, username+" "+StatusConstant.NOT_FOUND);
            }

            String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;

            if (!jwtUtil.validateToken(jwtToken, username)) {
                return new ResponseBO(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), null, "Invalid or expired JWT token");
            }

            CustomUserDetails customUserDetails = userService.loadUserByUsername(username);
            if (customUserDetails == null) {
                return new ResponseBO(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), null, "User not found");
            }

            UserDTO user = userServiceInterface.entitytodto(customUserDetails.getUser());
            if (!Objects.equals(user.getEmailId(), superAdminEmail)) {
            if (user.getOrganization().getOrganizationSubscription().getSubscription() == null) {
                return new ResponseBO(HttpStatus.NO_CONTENT.value(), HttpStatus.NO_CONTENT.getReasonPhrase(), user, "User has not subscribed yet");
            }}

            return new ResponseBO(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), user, "User details retrieved successfully");

        } catch (IllegalArgumentException e) {
            LOGGER.error("JWT validation error: {}", e.getMessage());
            return new ResponseBO(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), null, e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error retrieving user details: {}", e.getMessage(), e);
            return new ResponseBO(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, "Internal server error");
        }
    }

    @PostMapping("/logout/{username}")
    @ApiOperation(value = "Logout User", notes = "This endpoint allows users Logout with the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Logout successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO logout(@PathVariable("username") String username) {
        try {
            if (iLoginServiceInterface.isUsernameExists(username)){
                LOGGER.info("Entering into Logout Successfully");
                iLoginServiceInterface.logout(username);
                LOGGER.info("Exiting Logout Successfully");
                return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), null, "Logout Successfully");
            } else {
                LOGGER.info("Username Incorrect");
                return new ResponseBO(org.apache.http.HttpStatus.SC_UNAUTHORIZED,HttpStatus.UNAUTHORIZED.getReasonPhrase(), null, username + "Incorrect");
            }
        }   catch (Exception ex) {
            LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/login/resetPassword/{username}")
    @ApiOperation(value = "Reset User's Password", notes = "This endpoint allows to reset password with the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Reset Password Get successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "Invalid Password in the system"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO resetPassword(@RequestBody LoginDTO loginDTO, @PathVariable String username) {
        try {
            LOGGER.info("Entering Into Reset Password Successfully");
            String reset = iLoginServiceInterface.resetPassword(username, loginDTO.getPassword());
            LOGGER.info("Exiting Into Reset Password Successfully");

            if (Objects.equals(reset, "active")) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), null, "Password reset successfully");
            }if (Objects.equals(reset, "inActive")) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), null, "Password not reset");
            } else {
                return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase(), null, username +" "+ StatusConstant.NOT_FOUND);
            }

        } catch (Exception ex) {
            LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }


}

