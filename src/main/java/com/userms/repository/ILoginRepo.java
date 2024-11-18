package com.userms.repository;

import com.userms.entity.LoginEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILoginRepo extends JpaRepository<LoginEntity,Long> {

    LoginEntity findByUsername(String username);

    boolean existsByUsername(String username);
}
