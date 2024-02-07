package com.satvik.satchat.repository;

import com.satvik.satchat.entity.RoleEntity;
import com.satvik.satchat.model.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {
    Optional<RoleEntity> findByName(ERole name);
}
