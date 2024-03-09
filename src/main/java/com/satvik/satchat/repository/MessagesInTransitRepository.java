package com.satvik.satchat.repository;

import com.satvik.satchat.entity.MessagesInTransitEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessagesInTransitRepository extends JpaRepository<MessagesInTransitEntity, UUID> {

  @Query(
      "select m from MessagesInTransitEntity m where m.toUser = :toUser and m.read = false and m.fromUser = :fromUser")
  List<MessagesInTransitEntity> findUnseenMessages(
      @Param("toUser") UUID toUser, @Param("fromUser") UUID fromUser);

  @Query(
      value =
          "select from_user, count(1) as count from messages_in_transit where to_user = :toUser and read=false group by from_user",
      nativeQuery = true)
  List<Object[]> findUnseenMessagesCount(@Param("toUser") UUID toUser);
}
