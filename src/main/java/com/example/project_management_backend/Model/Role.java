package com.example.project_management_backend.Model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID roleId;

    @Column(nullable = false, unique = true)
    private String roleName;
    

   
    public Role() {}

    
    public Role(String roleName) {
        this.roleName = roleName;
    }

    
    public Role(UUID roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    
    public UUID getRoleId() {
        return roleId;
    }

    
    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }

    
    public String getRoleName() {
        return roleName;
    }

    
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}