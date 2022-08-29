// filter that executes once per request
// An authentication filter is the main point from which every authentication request is coming.

package com.demo.project2.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.util.StringUtils;

import org.springframework.web.filter.OncePerRequestFilter;

import com.demo.project2.security.services.UserDetailsServiceImpl;


public class AuthTokenFilter extends OncePerRequestFilter {

    // OncePerRequestFilter makes a single execution for each request to our API.
    // It provides a doFilterInternal() method that we implement parsing & validating JWT,
    // loading User details (using UserDetailsService), checking Authorization (using UsernamePasswordAuthenticationToken).

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String jwt = parseJwt(request);   // get JWT from the Authorization header

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) { // JWT exists and is valid

                String email = jwtUtils.getEmailFromJwtToken(jwt); // parse email from JWT

                UserDetails userDetails = userDetailsService.loadUserByUsername(email); // get user details using email

                // create authentication object using userDetails
                    // UsernamePasswordAuthenticationToken gets {email, password} from login Request,
                    // AuthenticationManager will use it to authenticate a login account.
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // set the userDetails in SecurityContext using setAuthentication(authentication) method.
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        // get JWT from the Authorization header (by removing Bearer prefix)

        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            // NB the access token should begin with the word 'Bearer' e.g."Bearer eyJhbGciOiJIUzUxMiJ9....""

            return headerAuth.substring(7, headerAuth.length());
        }
        return null;
    }
}
