package com.userms.repository;

import com.userms.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRoleRepo extends JpaRepository<RoleEntity, Long> {
    boolean existsByRoleId(Long roleId);

    boolean existsByRole(String role);

    RoleEntity findByRole(String role);
}
