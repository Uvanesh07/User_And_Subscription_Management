package com.userms.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "organization" )
public class OrganizationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long orgId;

    @Column(name = "organization_name")
    private String organizationName;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "gstin",unique = true,nullable = false)
    private String gstin;

    @Column(name = "pan",unique = true,nullable = false)
    private String pan;

    @Column(name = "tan",unique = true,nullable = false)
    private String tan;

    @Column(name = "organization_type")
    private String organizationType;

    @Column(name = "incorporation_date")
    private LocalDate incorporationDate;

    @Column(name = "cin",unique = true,nullable = false)
    private String cin;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "organization_subscription_id",referencedColumnName = "org_subscription_id")
    private OrganizationSubscriptionEntity organizationSubscription;


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

    public OrganizationSubscriptionEntity getOrganizationSubscription() {
        return organizationSubscription;
    }

    public void setOrganizationSubscription(OrganizationSubscriptionEntity organizationSubscription) {
        this.organizationSubscription = organizationSubscription;
    }
}
