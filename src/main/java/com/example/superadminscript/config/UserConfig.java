package com.example.superadminscript.config;

import com.example.superadminscript.entity.Permission;
import com.example.superadminscript.entity.Role;
import com.example.superadminscript.entity.RolePermission;
import com.example.superadminscript.entity.User;
import com.example.superadminscript.enums.Status;
import com.example.superadminscript.repo.PermissionRepository;
import com.example.superadminscript.repo.RolePermissionRepository;
import com.example.superadminscript.repo.RoleRepository;
import com.example.superadminscript.repo.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class UserConfig {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PermissionRepository permissionRepository;
    private RolePermissionRepository rolePermissionRepository;
    @Value("${super.admin.email}")
    private String superAdminEmail;

    public UserConfig(UserRepository userRepository, RoleRepository roleRepository, PermissionRepository permissionRepository, RolePermissionRepository rolePermissionRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @PostConstruct
    private void run() {
        if(!userRepository.existsByEmail(superAdminEmail)){
            Role role = createRole();
            if(Optional.ofNullable(role).isPresent()){
                List<Permission> permissionList = createPerMission();
                if(!permissionList.isEmpty()){
                    User user = userRepository.save(User.builder()
                            .firstName("FIRST_NAME")
                            .lastName("LAST_NAME")
                            .email(superAdminEmail)
                            .build());
                    RolePermission rolePermission = null;
                    for (Permission permission : permissionList) {
                        rolePermission = rolePermissionRepository.save(RolePermission.builder()
                                .role(role)
                                .permission(permission)
                                .user(user)
                                .build());
                    }
                }
            }
        }
    }

    private Role createRole(){
        if(!roleRepository.existsByRoleName("SUPER_ADMIN")){
            return roleRepository.save(
                Role.builder()
                        .roleName("SUPER_ADMIN")
                        .status(Status.ACTIVE)
                        .build()
            );
        }
        return null;
    }

    private List<Permission> createPerMission(){
        List<Permission> permissionList = new ArrayList<>();
        if(!permissionRepository.existsByPermissionName("CREATE"))
            permissionList.add(Permission.builder().permissionName("CREATE").build());
        if(!permissionRepository.existsByPermissionName("READ"))
            permissionList.add(Permission.builder().permissionName("READ").build());
        if(!permissionRepository.existsByPermissionName("UPDATE"))
            permissionList.add(Permission.builder().permissionName("UPDATE").build());
        if(!permissionRepository.existsByPermissionName("DELETE"))
            permissionList.add(Permission.builder().permissionName("DELETE").build());
        if(!permissionList.isEmpty()) {
            permissionList = permissionRepository.saveAll(permissionList);
        }
        return permissionList;
    }
}
