package com.satvik.satchat.repository;

import com.satvik.satchat.entity.MessagesInTransitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MessagesInTransitRepository extends JpaRepository<MessagesInTransitEntity, UUID> {
}
