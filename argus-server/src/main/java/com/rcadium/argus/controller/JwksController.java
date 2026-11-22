package com.rcadium.argus.controller;

import com.rcadium.argus.service.JwtKeyService;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

@RestController
public class JwksController {

    private final JwtKeyService keyService;

    JwksController(JwtKeyService keyService){
        this.keyService = keyService;
    }

    public ResponseEntity<Map<String, Object>> jwks(){
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofMinutes(5)).cachePublic())
                .body(keyService.publicJwtSet());
    }
}
