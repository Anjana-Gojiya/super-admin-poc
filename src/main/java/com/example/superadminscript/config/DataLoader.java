package com.example.superadminscript.config;

import com.example.superadminscript.async.EmailAsyncService;
import com.example.superadminscript.entity.Permission;
import com.example.superadminscript.entity.Role;
import com.example.superadminscript.entity.RolePermission;
import com.example.superadminscript.entity.User;
import com.example.superadminscript.enums.Status;
import com.example.superadminscript.repo.PermissionRepository;
import com.example.superadminscript.repo.RolePermissionRepository;
import com.example.superadminscript.repo.RoleRepository;
import com.example.superadminscript.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DataLoader implements CommandLineRunner {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PermissionRepository permissionRepository;
    private RolePermissionRepository rolePermissionRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private EmailAsyncService emailAsyncService;
    @Value("${super.admin.email}")
    private String superAdminEmail;

    public DataLoader(UserRepository userRepository, RoleRepository roleRepository, PermissionRepository permissionRepository, RolePermissionRepository rolePermissionRepository, BCryptPasswordEncoder bCryptPasswordEncoder, EmailAsyncService emailAsyncService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.emailAsyncService = emailAsyncService;
    }

    @Override
    public void run(String... args) throws Exception {
        if(!userRepository.existsByEmail(superAdminEmail)){
            Role role = createRole();
            if(Optional.ofNullable(role).isPresent()){
                List<Permission> permissionList = createPerMission();
                if(!permissionList.isEmpty()){
                    User user = userRepository.save(User.builder()
                            .firstName("FIRST_NAME")
                            .lastName("LAST_NAME")
                            .email(superAdminEmail)
                            .password(bCryptPasswordEncoder.encode("Admin@123"))
                            .build());
                    List<RolePermission> rolePermissionList = new ArrayList<>();
                    for (Permission permission : permissionList) {
                        rolePermissionList.add(RolePermission.builder()
                                .role(role)
                                .permission(permission)
                                .user(user)
                                .build());
                    }
                    rolePermissionRepository.saveAll(rolePermissionList);
                    //send super admin credentials in email
                    Map<String,String> map = new HashMap<>();
                    map.put("email",user.getEmail());
                    map.put("password","Admin@123");
                    map.put("firstName",user.getFirstName());
                    List<Object> arguments = new ArrayList<>();
                    arguments.add(map);
                    emailAsyncService.sendSuperAdminCredentials(arguments);
                }
            }
        }
    }

    private Role createRole(){
        if(!roleRepository.existsByRoleName("SUPER_ADMIN")){
            return roleRepository.save(Role.builder()
                    .roleName("SUPER_ADMIN")
                    .status(Status.ACTIVE)
                    .build());
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
