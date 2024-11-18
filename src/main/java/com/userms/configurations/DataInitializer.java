package com.userms.configurations;

import com.userms.DTO.*;
import com.userms.controller.RoleController;
import com.userms.controller.UserController;
import com.userms.entity.RoleEntity;
import com.userms.entity.UserEntity;
import com.userms.repository.IRoleRepo;
import com.userms.repository.IUserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class DataInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleController roleController;
    private final UserController userController;
    private final IRoleRepo roleRepository;
    private final IUserRepo userRepository;

    @Value("${superAdmin.role}")
    private String superAdminRole;
    @Value("${superAdmin.description}")
    private String superAdminDescription;
    @Value("${superAdmin.email}")
    private String superAdminEmail;
    @Value("${superAdmin.password}")
    private String superAdminPassword;
    @Value("${superAdmin.firstname}")
    private String superAdminFirstName;
    @Value("${superAdmin.lastname}")
    private String superAdminLastName;
    @Value("${superAdmin.mobile}")
    private String superAdminMobile;
    @Value("${superAdmin.address.line1}")
    private String superAdminAddressLine1;
    @Value("${superAdmin.address.line2}")
    private String superAdminAddressLine2;
    @Value("${superAdmin.address.city}")
    private String superAdminCity;
    @Value("${superAdmin.address.state}")
    private String superAdminState;
    @Value("${superAdmin.address.country}")
    private String superAdminCountry;
    @Value("${superAdmin.address.pincode}")
    private String superAdminPincode;
    @Value("${superAdmin.organization.name}")
    private String organizationName;
    @Value("${superAdmin.organization.displayName}")
    private String organizationDisplayName;
    @Value("${superAdmin.organization.gstin}")
    private String organizationGstin;
    @Value("${superAdmin.organization.pan}")
    private String organizationPan;
    @Value("${superAdmin.organization.tan}")
    private String organizationTan;
    @Value("${superAdmin.organization.type}")
    private String organizationType;
    @Value("${superAdmin.organization.incorporationDate}")
    private String incorporationDateString;
    @Value("${superAdmin.organization.cin}")
    private String organizationCin;

    private boolean isInitialized = false;

    @Autowired
    public DataInitializer(RoleController roleController, UserController userController, IRoleRepo roleRepository, IUserRepo userRepository) {
        this.roleController = roleController;
        this.userController = userController;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent() {
        initializeOnce();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduledTask() {
        initializeOnce();
    }

    private synchronized void initializeOnce() {
        if (!isInitialized) {
            createDefaultRole();
            createDefaultUser();
            isInitialized = true;
            LOGGER.info("Initialization completed.");
        } else {
            LOGGER.info("Initialization already completed. Skipping.");
        }
    }

    private void createDefaultRole() {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setRole(superAdminRole);
        roleDTO.setDescription(superAdminDescription);

        RoleEntity existingRoleEntity = roleRepository.findByRole(superAdminRole);
        if (existingRoleEntity != null) {
            if (existingRoleEntity.getDescription().equals(superAdminDescription)) {
                LOGGER.info("Default role {} already exists with the same data", superAdminRole);
            } else {
                roleDTO.setRoleId(existingRoleEntity.getRoleId());
                ResponseBO updateResponse = roleController.updateRole(roleDTO, existingRoleEntity.getRoleId());
                if (updateResponse.getCode() == org.apache.http.HttpStatus.SC_OK) {
                    LOGGER.info("Default role {} updated successfully", superAdminRole);
                } else {
                    LOGGER.warn("Failed to update default role: {}", updateResponse.getMsg());
                }
            }
        } else {
            ResponseBO createResponse = roleController.createRole(roleDTO);
            if (createResponse.getCode() == org.apache.http.HttpStatus.SC_CREATED) {
                LOGGER.info("Default role {} created successfully", superAdminRole);
            } else {
                LOGGER.warn("Failed to create default role: {}", createResponse.getMsg());
            }
        }
    }

    private boolean isUserMatchingDefaultData(UserEntity existingUserEntity) {
        return existingUserEntity.getFirstName().equals(superAdminFirstName) &&
                existingUserEntity.getLastName().equals(superAdminLastName) &&
                existingUserEntity.getEmailId().equals(superAdminEmail) &&
                existingUserEntity.getMobileNo().equals(superAdminMobile) &&
                existingUserEntity.getAddress().getAddressLine1().equals(superAdminAddressLine1) &&
                existingUserEntity.getAddress().getAddressLine2().equals(superAdminAddressLine2) &&
                existingUserEntity.getAddress().getCity().equals(superAdminCity) &&
                existingUserEntity.getAddress().getState().equals(superAdminState) &&
                existingUserEntity.getAddress().getCountry().equals(superAdminCountry) &&
                existingUserEntity.getAddress().getPincode().equals(superAdminPincode) &&
                existingUserEntity.getOrganization().getOrganizationName().equals(organizationName) &&
                existingUserEntity.getOrganization().getDisplayName().equals(organizationDisplayName) &&
                existingUserEntity.getOrganization().getGstin().equals(organizationGstin) &&
                existingUserEntity.getOrganization().getPan().equals(organizationPan) &&
                existingUserEntity.getOrganization().getTan().equals(organizationTan) &&
                existingUserEntity.getOrganization().getOrganizationType().equals(organizationType) &&
                existingUserEntity.getOrganization().getIncorporationDate().equals(LocalDate.parse(incorporationDateString)) &&
                existingUserEntity.getOrganization().getCin().equals(organizationCin);
    }

    private void createDefaultUser() {
        UserEntity existingUserEntity = userRepository.findByEmailId(superAdminEmail);
        if (existingUserEntity != null) {
            if (isUserMatchingDefaultData(existingUserEntity)) {
                LOGGER.info("Default user with email {} already exists with the same data", superAdminEmail);
            } else {
                UserDTO userDTO = buildUserDTO();
                ResponseBO updateResponse = userController.updateUser(userDTO, existingUserEntity.getUserId());
                if (updateResponse.getCode() == org.apache.http.HttpStatus.SC_OK) {
                    LOGGER.info("Default user {} updated successfully", superAdminFirstName + " " + superAdminLastName);
                } else {
                    LOGGER.warn("Failed to update default user: {}", updateResponse.getMsg());
                }
            }
        } else {
            UserDTO userDTO = buildUserDTO();
            ResponseBO createResponse = userController.registration(userDTO);
            if (createResponse.getCode() == org.apache.http.HttpStatus.SC_CREATED) {
                LOGGER.info("Default user {} created successfully", superAdminFirstName + " " + superAdminLastName);
            } else {
                LOGGER.warn("Failed to create default user: {}", createResponse.getMsg());
            }
        }
    }

    private UserDTO buildUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName(superAdminFirstName);
        userDTO.setLastName(superAdminLastName);
        userDTO.setEmailId(superAdminEmail);
        userDTO.setMobileNo(superAdminMobile);

        AddressDTO address = new AddressDTO();
        address.setAddressLine1(superAdminAddressLine1);
        address.setAddressLine2(superAdminAddressLine2);
        address.setCity(superAdminCity);
        address.setState(superAdminState);
        address.setCountry(superAdminCountry);
        address.setPincode(superAdminPincode);
        userDTO.setAddress(address);

        OrganizationDTO organization = new OrganizationDTO();
        organization.setOrganizationName(organizationName);
        organization.setDisplayName(organizationDisplayName);
        organization.setGstin(organizationGstin);
        organization.setPan(organizationPan);
        organization.setTan(organizationTan);
        organization.setOrganizationType(organizationType);
        organization.setIncorporationDate(LocalDate.parse(incorporationDateString));
        organization.setCin(organizationCin);
        userDTO.setOrganization(organization);

        return userDTO;
    }
}
