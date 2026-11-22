package com.rcadium.argus.repository;

import com.rcadium.argus.entity.RoleEntity;
import com.rcadium.argus.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleJpaRepository extends JpaRepository<RoleEntity, UUID> {

    Optional<UserEntity> findByName(String name);
}
