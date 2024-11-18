package com.userms.DTO;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserDTO {

    private Long userId;

    private String customerId;

    @NotNull(message = "First Name is required")
    @NotEmpty(message = "First Name should not be empty")
    @Size(min = 3, max = 50, message = "First Name should be between 3 and 50 characters")
    private String firstName;

    @NotNull(message = "Last Name is required")
    @NotEmpty(message = "Last Name should not be empty")
    @Size(min = 3, max = 50, message = "Last Name should be between 3 and 50 characters")
    private String lastName;

    @NotNull(message = "Email Id is required")
    @NotEmpty(message = "Email Id should not be empty")
    @Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Invalid email format")
    private String emailId;

    @NotNull(message = "Mobile No is required")
    @NotEmpty(message = "Mobile No should not be empty")
    @Size(min = 3, max = 50, message = "Mobile No should be between 3 and 50 characters")
    @Pattern(regexp = "^\\+?[0-9\\-]*$", message = "Invalid mobile number format")
    private String mobileNo;

    @NotNull(message = "Role Id is required")
    private Long roleId;

    @NotNull(message = "Subscription Id is required")
    private Long subscriptionId;

    @NotNull(message = "Address is required")
    private AddressDTO address;

    @NotNull(message = "Organization is required")
    private OrganizationDTO organization;

    private RoleDTO role;

    private PermissionDTO permission;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public RoleDTO getRole() {
        return role;
    }

    public void setRole(RoleDTO role) {
        this.role = role;
    }

    public PermissionDTO getPermission() {
        return permission;
    }

    public void setPermission(PermissionDTO permission) {
        this.permission = permission;
    }
}




