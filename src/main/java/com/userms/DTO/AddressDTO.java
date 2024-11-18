package com.userms.DTO;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class AddressDTO {

    private Long addressId;

    @NotNull(message = "Address Line 1 is required")
    @NotEmpty(message = "Address Line 1 should not be empty")
    @Size(min = 3, max = 50, message = "Address Line 1 must be between 3 and 50 characters")
    private String addressLine1;

    @Size(max = 50, message = "Address Line 2 cannot exceed 50 characters")
    private String addressLine2;

    @NotNull(message = "City is required")
    @NotEmpty(message = "City should not be empty")
    @Size(min = 3, max = 50, message = "City must be between 3 and 50 characters")
    private String city;

    @NotNull(message = "State is required")
    @NotEmpty(message = "State should not be empty")
    @Size(min = 3, max = 50, message = "State must be between 3 and 50 characters")
    private String state;

    @NotNull(message = "Country is required")
    @NotEmpty(message = "Country should not be empty")
    @Size(min = 3, max = 50, message = "Country must be between 3 and 50 characters")
    private String country;

    @NotNull(message = "Pincode is required")
    @NotEmpty(message = "Pincode should not be empty")
    @Size(min = 3, max = 10, message = "Pincode must be between 3 and 10 characters")
    @Pattern(regexp = "\\b\\d{3,10}\\b", message = "Pincode must be a valid numeric value between 3 and 10 digits")
    private String pincode;

    private Long referenceId;

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }
}

