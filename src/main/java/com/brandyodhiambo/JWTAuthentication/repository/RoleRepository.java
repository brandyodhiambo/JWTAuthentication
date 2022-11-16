package com.brandyodhiambo.JWTAuthentication.repository;

import com.brandyodhiambo.JWTAuthentication.model.ERole;
import com.brandyodhiambo.JWTAuthentication.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
