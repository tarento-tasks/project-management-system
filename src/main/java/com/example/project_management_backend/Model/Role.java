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

    // ✅ Default Constructor (Needed by JPA)
    public Role() {}

    // ✅ Constructor with roleName (for creating new roles)
    public Role(String roleName) {
        this.roleName = roleName;
    }

    // ✅ Constructor with roleId and roleName (Fix for your issue)
    public Role(UUID roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    // ✅ Getter for roleId
    public UUID getRoleId() {
        return roleId;
    }

    // ✅ Setter for roleId
    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }

    // ✅ Getter for roleName
    public String getRoleName() {
        return roleName;
    }

    // ✅ Setter for roleName
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
