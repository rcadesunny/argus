package com.rcadium.argus.service;

public record TokenPair(
        String accessToken, String refreshToken, long expiresInSeconds
) {
}
