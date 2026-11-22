package com.rcadium.argus.domain.port;

import com.rcadium.argus.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    User save(User user);
}
