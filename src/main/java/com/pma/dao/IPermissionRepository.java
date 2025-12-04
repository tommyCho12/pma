package com.pma.dao;

import com.pma.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IPermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
}
