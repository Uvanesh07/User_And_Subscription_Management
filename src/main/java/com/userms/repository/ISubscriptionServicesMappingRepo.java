package com.userms.repository;

import com.userms.entity.SubscriptionServicesMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISubscriptionServicesMappingRepo extends JpaRepository<SubscriptionServicesMapping, Long> {

    @Query("SELECT COUNT(ssm) > 0 FROM SubscriptionServicesMapping ssm " +
            "JOIN ssm.subscriptionEntity se " +
            "JOIN ssm.serviceEntity sev " +
            "WHERE se.subscriptionId = :subscriptionId " +
            "AND sev.serviceId = :serviceId")
    boolean existsBySubscriptionIdAndServiceId(Long subscriptionId, Long serviceId);

    @Query("SELECT COUNT(ssm) > 1 FROM SubscriptionServicesMapping ssm " +
            "JOIN ssm.serviceEntity sev " +
            "WHERE sev.serviceId = :serviceId")
    boolean existsDuplicateServiceId(Long serviceId);

    List<SubscriptionServicesMapping> findBySubscriptionEntitySubscriptionIdAndServiceEntityServiceId(Long subscriptionId, Long serviceId);

}


