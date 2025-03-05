package com.example.project_management_backend.Repository;
import com.example.project_management_backend.Model.SkillMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;
@Repository
public interface SkillMappingRepository extends JpaRepository<SkillMapping, UUID> {
    List<SkillMapping> findByUserId(UUID userId);
}


