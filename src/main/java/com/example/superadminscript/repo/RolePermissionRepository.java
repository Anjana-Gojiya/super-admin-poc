package com.example.superadminscript.repo;

import com.example.superadminscript.entity.Permission;
import com.example.superadminscript.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {
}
