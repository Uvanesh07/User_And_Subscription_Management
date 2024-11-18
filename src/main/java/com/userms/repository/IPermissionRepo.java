package com.userms.repository;

import com.userms.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IPermissionRepo extends JpaRepository<PermissionEntity, Long> {

    boolean existsByPermissionId(Long permissionId);

    boolean existsByPermissionName(String permissionName);

    PermissionEntity findByUserUserId(Long userId);

    @Query("SELECT MAX(u.permissionId) FROM PermissionEntity u")
    Long findLastPermissionId();
}
