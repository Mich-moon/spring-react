package com.demo.project2.security.services;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.project2.exception.TokenRefreshException;
import com.demo.project2.model.RefreshToken;
import com.demo.project2.repository.RefreshTokenRepository;
import com.demo.project2.repository.UserRepository;

@Service
public class RefreshTokenService {

    @Value("${project.app.jwtRefreshExpirationMs}") // from application.properties file.
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    // methods
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId) {

        // create new refresh token and set its user, expiryDate, token then save it.

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }
    public RefreshToken verifyExpiration(RefreshToken token) {

        // verify if refreshToken is expired by comparing its expiration time to current time

        if (token.getExpiryDate().compareTo(Instant.now()) < 0) { // refreshToken is expired
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new login request");
        }
        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {

        // delete refreshToken based on its user

        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }
}
