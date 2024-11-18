package com.userms.DTO;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class ServiceDTO {
    private Long serviceId;

    @NotNull(message = "Service name is required")
    @NotEmpty(message = "Service name should not be empty")
    private String serviceName;

    @NotNull(message = "Description is required")
    @NotEmpty(message = "Description should not be empty")
    private String description;

    @NotNull(message = "Active Status is required")
    private Boolean activeStatus;

    private List<FeatureDTO> feature = new ArrayList<>();

    @NotNull(message = "Subscription Id is required")
    private Long subscriptionId;

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(Boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    public List<FeatureDTO> getFeature() {
        return feature;
    }

    public void setFeature(List<FeatureDTO> feature) {
        this.feature = feature;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }
}
