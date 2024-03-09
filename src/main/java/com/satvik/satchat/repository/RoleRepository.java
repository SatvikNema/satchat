package com.satvik.satchat.repository;

import com.satvik.satchat.entity.RoleEntity;
import com.satvik.satchat.model.ERole;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {
  Optional<RoleEntity> findByName(ERole name);
}
