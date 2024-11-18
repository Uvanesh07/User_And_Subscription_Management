package com.userms.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "organization_subscription_details" )
public class OrganizationSubscriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "org_subscription_id")
    private Long orgSubscriptionId;

    @Column(name = "subscription_date")
    private LocalDate subscriptionDate;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private SubscriptionEntity subscription;

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

    public SubscriptionEntity getSubscription() {
        return subscription;
    }

    public void setSubscription(SubscriptionEntity subscription) {
        this.subscription = subscription;
    }
}
