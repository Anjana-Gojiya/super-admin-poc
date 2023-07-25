package com.example.superadminscript.entity;

import com.example.superadminscript.dto.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RolePermission extends BaseEntity {

    @ManyToOne
    private Role role;
    @ManyToOne
    private Permission permission;
    @ManyToOne
    private User user;
}
