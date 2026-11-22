package com.rcadium.argus.infrastructure.persistence.mapper;

import com.rcadium.argus.domain.Permission;
import com.rcadium.argus.domain.Role;
import com.rcadium.argus.domain.User;
import com.rcadium.argus.domain.UserStatus;
import com.rcadium.argus.infrastructure.persistence.entity.PermissionEntity;
import com.rcadium.argus.infrastructure.persistence.entity.RoleEntity;
import com.rcadium.argus.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserPersistenceMapper {

    public User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                UserStatus.valueOf(entity.getStatus()),
                toDomainRoles(entity.getRoles()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public UserEntity toEntity(User user, Set<RoleEntity> roles) {
        return new UserEntity(
                user.id(),
                user.email(),
                user.passwordHash(),
                user.status().name(),
                roles,
                user.createdAt(),
                user.updatedAt()
        );
    }

    private Set<Role> toDomainRoles(Set<RoleEntity> entities) {
        return entities.stream()
                .map(this::toDomainRole)
                .collect(Collectors.toUnmodifiableSet());
    }

    private Role toDomainRole(RoleEntity entity) {
        Set<Permission> permissions = entity.getPermissions().stream()
                .map(PermissionEntity::getName)
                .map(Permission::new)
                .collect(Collectors.toUnmodifiableSet());

        return new Role(entity.getName(), permissions);
    }
}
