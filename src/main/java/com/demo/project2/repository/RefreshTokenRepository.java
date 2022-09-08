package com.demo.project2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.demo.project2.model.RefreshToken;
import com.demo.project2.model.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Override
    Optional<RefreshToken> findById(Long id);
    Optional<RefreshToken> findByToken(String token);

    int deleteByUser(User user);
}