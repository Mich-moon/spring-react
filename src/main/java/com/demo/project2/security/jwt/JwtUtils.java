// JWT utility class for JWT generation, validation and parsing

package com.demo.project2.security.jwt;

import java.security.SignatureException;
import java.util.Date;

import io.jsonwebtoken.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.demo.project2.security.services.UserDetailsImpl;

import static com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm.*;
import static io.jsonwebtoken.SignatureAlgorithm.HS512;


@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${project.app.jwtSecret}")  // from application.properties file.
    private String jwtSecret;

    @Value("${project.app.jwtExpirationMs}")  // from application.properties file.
    private int jwtExpirationMs;


    // methods

    public String generateJwtToken(Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal(); // get user details from security context

        // generate a JWT from email, date, expiration, secret
        return Jwts.builder()
                .setSubject((userPrincipal.getEmail()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(HS512, jwtSecret)
                .compact();

    }

    public String getEmailFromJwtToken(String token) {

        // get email form JWT
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {

        // validate JWT

        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
/*
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
*/
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());

        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());

        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());

        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());

        }
        return false;
    }

    public String generateTokenFromEmail(String email) {

        // generate (refresh) token using email
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

}
