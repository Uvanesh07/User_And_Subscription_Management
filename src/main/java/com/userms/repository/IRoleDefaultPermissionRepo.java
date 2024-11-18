package com.userms.repository;

import com.userms.entity.RoleDefaultPermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRoleDefaultPermissionRepo extends JpaRepository<RoleDefaultPermissionEntity, Long> {

}
