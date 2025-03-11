package com.example.project_management_backend.Controller;
import org.springframework.dao.DataIntegrityViolationException;
import com.example.project_management_backend.DTO.RoleDTO;
import com.example.project_management_backend.Model.Role;
import com.example.project_management_backend.Service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    
    public List<RoleDTO> getAllRoles() {
        return roleService.getAllRoles();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") 
    
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable UUID id) {
        Optional<RoleDTO> role = roleService.getRoleById(id);
        return role.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{roleName}")
    @PreAuthorize("hasRole('ADMIN')") 
    
    public ResponseEntity<RoleDTO> getRoleByName(@PathVariable String roleName) {
        Optional<RoleDTO> role = roleService.getRoleByName(roleName);
        return role.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

  
   

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") 
    public ResponseEntity<?> createRole(@RequestBody Role role) {
    try {
        Role savedRole = roleService.saveRole(role);
        return ResponseEntity.ok(savedRole);
    } catch (DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().body("Role name already exists");
    }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") 
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}