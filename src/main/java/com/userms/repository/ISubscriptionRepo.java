package com.userms.repository;

import com.userms.entity.SubscriptionEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISubscriptionRepo extends JpaRepository<SubscriptionEntity, Long> {
    boolean existsBySubscriptionId(Long subscriptionId);

    boolean existsBySubscriptionName(String subscriptionName);

    @Query("SELECT s FROM SubscriptionEntity s WHERE s.activeStatus = true")
    List<SubscriptionEntity> findActiveSubscriptions(Pageable pageable);
}
