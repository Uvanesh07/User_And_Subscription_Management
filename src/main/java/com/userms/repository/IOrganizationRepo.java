package com.userms.repository;

import com.userms.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOrganizationRepo extends JpaRepository<OrganizationEntity,Long> {
    boolean existsByGstin(String gstin);
    boolean existsByPan(String pan);
    boolean existsByTan(String tan);
    boolean existsByCin(String cin);
}
