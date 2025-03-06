package com.example.project_management_backend.Service;

import com.example.project_management_backend.DTO.RoleDTO;
import com.example.project_management_backend.Model.Role;
import com.example.project_management_backend.Repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> new RoleDTO(role.getRoleId(), role.getRoleName()))
                .collect(Collectors.toList());
    }

    public Optional<RoleDTO> getRoleById(UUID roleId) {
        return roleRepository.findById(roleId)
                .map(role -> new RoleDTO(role.getRoleId(), role.getRoleName()));
    }

    public Optional<RoleDTO> getRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName)
                .map(role -> new RoleDTO(role.getRoleId(), role.getRoleName()));
    }

    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    public void deleteRole(UUID roleId) {
        roleRepository.deleteById(roleId);
    }
}