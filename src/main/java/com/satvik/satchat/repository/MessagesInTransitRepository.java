package com.satvik.satchat.repository;

import com.satvik.satchat.entity.MessagesInTransitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessagesInTransitRepository extends JpaRepository<MessagesInTransitEntity, UUID> {

    @Query("select m from MessagesInTransitEntity m where m.toUser = :toUser and m.read = false")
    List<MessagesInTransitEntity> findUnseenMessages(@Param("toUser") UUID toUser);
}
