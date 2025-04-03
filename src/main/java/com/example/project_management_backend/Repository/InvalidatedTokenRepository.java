
package com.example.project_management_backend.Repository;

import com.example.project_management_backend.Model.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
    Optional<InvalidatedToken> findByToken(String token);
}
