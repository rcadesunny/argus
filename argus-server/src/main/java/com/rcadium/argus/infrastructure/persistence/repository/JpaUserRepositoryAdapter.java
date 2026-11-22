package com.rcadium.argus.infrastructure.persistence.repository;

import com.rcadium.argus.domain.User;
import com.rcadium.argus.domain.port.UserRepository;
import com.rcadium.argus.infrastructure.persistence.entity.RoleEntity;
import com.rcadium.argus.infrastructure.persistence.entity.UserEntity;
import com.rcadium.argus.infrastructure.persistence.mapper.UserPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class JpaUserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository users;
    private final RoleJpaRepository roles;
    private final UserPersistenceMapper mapper;

    public JpaUserRepositoryAdapter(
            UserJpaRepository users,
                                    RoleJpaRepository roles,
                                    UserPersistenceMapper mapper) {
        this.users = users;
        this.roles = roles;
        this.mapper = mapper;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return users.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.findByEmailIgnoreCase(email).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.existsByEmailIgnoreCase(email);
    }

    @Override
    public User save(User user) {
        Set<RoleEntity> roleEntities = user.roles().stream()
                .map(role -> roles.findByName(role.name())
                        .orElseThrow(() -> new IllegalStateException("Unknown role: " + role.name())))
                .collect(Collectors.toSet());

        UserEntity saved = users.save(mapper.toEntity(user, roleEntities));
        return mapper.toDomain(saved);
    }
}
