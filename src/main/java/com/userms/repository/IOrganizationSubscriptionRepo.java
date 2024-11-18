package com.userms.repository;

import com.userms.entity.OrganizationSubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOrganizationSubscriptionRepo extends JpaRepository<OrganizationSubscriptionEntity,Long> {
}
