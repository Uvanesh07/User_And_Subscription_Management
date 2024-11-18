package com.userms.DTO;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class FeatureDTO {
    private Long featureId;

    @NotNull(message = "Feature name is required")
    @NotEmpty(message = "Feature name should not be empty")
    @Size(min = 3, max = 50, message = "Feature name must be between 3 and 50 characters")
    private String featureName;

    @NotNull(message = "Description is required")
    @NotEmpty(message = "Description should not be empty")
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @NotNull(message = "Active Status is required")
    private Boolean activeStatus;

    @NotNull(message = "Service Id is required")
    private Long serviceId;

    public Long getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Long featureId) {
        this.featureId = featureId;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public Boolean getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(Boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }
}
