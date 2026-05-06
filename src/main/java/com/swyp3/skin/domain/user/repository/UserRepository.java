package com.swyp3.skin.domain.user.repository;

import com.swyp3.skin.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
        SELECT COUNT(u) 
        FROM User u
        WHERE u.createdAt >= :start
          AND u.createdAt < :end
""")
    long countByCreatedAtBetween(
            LocalDateTime start,
            LocalDateTime end
    );
}
