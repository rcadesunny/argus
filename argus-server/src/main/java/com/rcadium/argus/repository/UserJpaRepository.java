package com.rcadium.argus.repository;

import com.rcadium.argus.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {

    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    @Query("select u from UserEntity u where lower(u.email) = lower(:email)")
    Optional<UserEntity> findByEmailWithRoles(@Param("email") String email);

    @Query("select count(u) > 0 from UserEntity u where lower(u.email) = lower(:email)")
    boolean existsByEmail(String email);
}
