package com.userms.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OrganizationSubscriptionDTO {

    private Long orgSubscriptionId;

    private LocalDate subscriptionDate;

    private SubscriptionDTO subscription;

    public Long getOrgSubscriptionId() {
        return orgSubscriptionId;
    }

    public void setOrgSubscriptionId(Long orgSubscriptionId) {
        this.orgSubscriptionId = orgSubscriptionId;
    }

    public LocalDate getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(LocalDate subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    public SubscriptionDTO getSubscription() {
        return subscription;
    }

    public void setSubscription(SubscriptionDTO subscription) {
        this.subscription = subscription;
    }
}

