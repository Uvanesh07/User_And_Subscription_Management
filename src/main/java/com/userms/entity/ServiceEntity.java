package com.userms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="subscription_service")
public class ServiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long serviceId;

    @Column(name = "service_name",unique = true,nullable = false)
    private String serviceName;

    private String description;

    @Column(name = "active_status")
    private Boolean activeStatus;

    @ManyToMany
    @JoinTable(
            name = "subscription_services_mapping",
            joinColumns = @JoinColumn(name = "service_id"),
            inverseJoinColumns = @JoinColumn(name = "subscription_id")
    )
    @JsonIgnore
    private List<SubscriptionEntity> subscriptionEntity = new ArrayList<>();

    @OneToMany(mappedBy = "serviceEntity", cascade = CascadeType.ALL)
    private List<FeatureEntity> featureEntity = new ArrayList<>();

    public ServiceEntity() {

    }

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

    public List<SubscriptionEntity> getSubscriptionEntity() {
        return subscriptionEntity;
    }

    public void setSubscriptionEntity(List<SubscriptionEntity> subscriptionEntity) {
        this.subscriptionEntity = subscriptionEntity;
    }

    public List<FeatureEntity> getFeatureEntity() {
        return featureEntity;
    }

    public void setFeatureEntity(List<FeatureEntity> featureEntity) {
        this.featureEntity = featureEntity;
    }
}