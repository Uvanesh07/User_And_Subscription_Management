package com.userms.DTO;

import com.userms.entity.SubscriptionType;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class SubscriptionDTO {

    private Long subscriptionId;

    @NotNull(message = "Subscription Name is required")
    @NotEmpty(message = "Subscription Name should not be empty")
    private String subscriptionName;

    @NotNull(message = "Validity is required")
    @PositiveOrZero(message = "Validity should be a positive number or zero")
    private Long validity;

    @NotNull(message = "Cost is required")
    @PositiveOrZero(message = "Cost should be a positive number or zero")
    private BigDecimal cost;

    @NotNull(message = "Subscription Type is required")
    private SubscriptionType subscriptionType;

    @NotNull(message = "Active Status is required")
    private Boolean activeStatus;

    private List<ServiceDTO> service = new ArrayList<>();

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }

    public void setSubscriptionName(String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }

    public Long getValidity() {
        return validity;
    }

    public void setValidity(Long validity) {
        this.validity = validity;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public Boolean getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(Boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    public List<ServiceDTO> getService() {
        return service;
    }

    public void setService(List<ServiceDTO> service) {
        this.service = service;
    }
}
