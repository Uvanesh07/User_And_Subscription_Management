package com.userms.repository;

import com.userms.entity.FeatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFeatureRepo extends JpaRepository<FeatureEntity, Long> {

    boolean existsByFeatureId(Long featureId);

    boolean existsByFeatureName(String featureName);
}
