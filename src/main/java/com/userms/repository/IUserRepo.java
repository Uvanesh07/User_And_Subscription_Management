package com.userms.repository;

import com.userms.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface IUserRepo extends JpaRepository<UserEntity,Long> {

    UserEntity findByEmailId(String email);

    boolean existsByEmailId(String emailId);

    boolean existsByMobileNo(String mobileNo);

    boolean existsByUserId(Long userId);

    List<UserEntity> findByRoleRoleId(Long roleId);

    @Query("SELECT MAX(u.customerId) FROM UserEntity u")
    Long findLastCustomerId();

    UserEntity findByPermissionPermissionId(Long permissionId);

    List<UserEntity> findByOrganizationOrganizationSubscriptionSubscriptionSubscriptionId(Long subscriptionId);

    @Query("SELECT u FROM UserEntity u " +
            "WHERE LOWER(u.emailId) LIKE LOWER(CONCAT('%', :searchKey, '%')) OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchKey, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchKey, '%')) OR " +
            "LOWER(u.mobileNo) LIKE LOWER(CONCAT('%', :searchKey, '%'))")
    Page<UserEntity> searchUsers(@Param("searchKey") String searchKey, Pageable pageable);

    List<UserEntity> findAllByRoleRoleId(Long roleId);
}
