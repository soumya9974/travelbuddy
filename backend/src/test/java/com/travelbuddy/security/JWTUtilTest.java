package com.travelbuddy.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JWTUtilTest {

    private final JWTUtil jwtUtil = new JWTUtil();

    @Test
    void generateToken_thenExtractUsernameAndUserId() {
        String token = jwtUtil.generateToken("user@example.com", 42L);

        assertTrue(jwtUtil.validateToken(token));
        assertEquals("user@example.com", jwtUtil.extractUsername(token));
        assertEquals(42L, jwtUtil.extractUserId(token));
    }

    @Test
    void validateToken_invalidToken_shouldReturnFalse() {
        assertFalse(jwtUtil.validateToken("not-a-jwt"));
        assertNull(jwtUtil.extractUserId("not-a-jwt"));
    }
}
