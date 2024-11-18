package com.userms.DTO;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;


@Data
public class OrganizationDTO {
    private Long orgId;

    @NotNull(message = "Organization Name is required")
    @NotEmpty(message = "Organization Name should not be empty")
    @Size(message = "Organization Name must be between 3 and 50 characters", min = 3, max = 50)
    private String organizationName;

    @NotNull(message = "Display Name is required")
    @NotEmpty(message = "Display Name should not be empty")
    @Size(message = "Display Name must be between 3 and 50 characters", min = 3, max = 50)
    private String displayName;

    @NotNull(message = "GSTIN is required")
    @NotEmpty(message = "GSTIN should not be empty")
    @Size(message = "GSTIN must be between 15 and 15 characters", min = 15, max = 15)
    private String gstin;

    @NotNull(message = "PAN is required")
    @NotEmpty(message = "PAN should not be empty")
    @Size(message = "PAN must be 10 characters long", min = 10, max = 10)
    private String pan;

    @NotNull(message = "TAN is required")
    @NotEmpty(message = "TAN should not be empty")
    @Size(message = "TAN must be 10 characters long", min = 10, max = 10)
    @Pattern(regexp = "[A-Z]{4}[0-9]{5}[A-Z]{1}", message = "TAN should match the format ABCD12345E")
    private String tan;

    @NotNull(message = "Organization Type is required")
    @NotEmpty(message = "Organization Type should not be empty")
    @Size(message = "Organization Type must be between 3 and 50 characters", min = 3, max = 50)
    private String organizationType;

    @NotNull(message = "Incorporation Date is required")
    private LocalDate incorporationDate;

    @NotNull(message = "CIN is required")
    @NotEmpty(message = "CIN should not be empty")
    @Size(message = "CIN must be 21 characters long", min = 21, max = 21)
    @Pattern(regexp = "[A-Z]{1}[0-9]{5}[A-Z]{2}[0-9]{4}[A-Z]{1}[0-9]{6}", message = "CIN should match the format ABCDE12345FGH678910")
    private String cin;

    private OrganizationSubscriptionDTO organizationSubscription;

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getGstin() {
        return gstin;
    }

    public void setGstin(String gstin) {
        this.gstin = gstin;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getTan() {
        return tan;
    }

    public void setTan(String tan) {
        this.tan = tan;
    }

    public String getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(String organizationType) {
        this.organizationType = organizationType;
    }

    public LocalDate getIncorporationDate() {
        return incorporationDate;
    }

    public void setIncorporationDate(LocalDate incorporationDate) {
        this.incorporationDate = incorporationDate;
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public OrganizationSubscriptionDTO getOrganizationSubscription() {
        return organizationSubscription;
    }

    public void setOrganizationSubscription(OrganizationSubscriptionDTO organizationSubscription) {
        this.organizationSubscription = organizationSubscription;
    }
}

