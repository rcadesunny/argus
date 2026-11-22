package com.rcadium.argus.service;

import com.rcadium.argus.entity.UserEntity;

public record IssuedRefreshToken(
        String token, UserEntity user
) {
}
