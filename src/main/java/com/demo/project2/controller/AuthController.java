// Controller receives and handles request after it was filtered by OncePerRequestFilter.

package com.demo.project2.controller;

import com.demo.project2.exception.TokenRefreshException;
import com.demo.project2.model.RefreshToken;
import com.demo.project2.payload.request.TokenRefreshRequest;
import com.demo.project2.security.services.RefreshTokenService;
import io.jsonwebtoken.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;

import com.demo.project2.model.URole;
import com.demo.project2.model.Role;
import com.demo.project2.model.User;
import com.demo.project2.repository.UserRepository;
import com.demo.project2.repository.RoleRepository;

import com.demo.project2.payload.request.LoginRequest;
import com.demo.project2.payload.request.SignupRequest;

import com.demo.project2.security.jwt.JwtUtils;
import com.demo.project2.security.services.UserDetailsImpl;


@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600) // to avoid CORS issues:
@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RefreshTokenService refreshTokenService;


    // API endpoints

    @PostMapping("register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();  // for holding response details

        try {
            // check existing email
            if (userRepository.existsByEmail(signUpRequest.getEmail())) {

                map.put("message", "Error: Username is already taken!" );
                return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
            }

            // Create new user's account
            User user = new User(
                    signUpRequest.getFirstName(),
                    signUpRequest.getLastName(),
                    signUpRequest.getEmail(),
                    encoder.encode(signUpRequest.getPassword()));

            // new User has role of ROLE_USER if role not specified
            Set<String> strRoles = signUpRequest.getRole();
            Set<Role> roles = new HashSet<>();
            if (strRoles == null) {
                Role userRole = roleRepository.findByName(URole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(userRole);

            } else {
                strRoles.forEach(role -> {
                    switch (role) {
                        case "admin":
                            Role adminRole = roleRepository.findByName(URole.ROLE_ADMIN)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(adminRole);
                            break;

                        case "mod":
                            Role modRole = roleRepository.findByName(URole.ROLE_MODERATOR)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(modRole);
                            break;

                        default:
                            Role userRole = roleRepository.findByName(URole.ROLE_USER)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(userRole);
                    }
                });
            }

            //save User to database using UserRepository
            user.setRoles(roles);
            userRepository.save(user);
            map.put("message", "User registered successfully!" );
            map.put("user", user);
            return new ResponseEntity<>(map, HttpStatus.CREATED);

        } catch (Exception ex) {    // creation unsuccessful
            map.clear();
            //map.put("message", "Oops, something went wrong" );
            map.put("message", ex.toString() );
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    @PostMapping("login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();  // for holding response details

        try {
            // authenticate { email, password }
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            // update SecurityContext using Authentication object
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // generate JWT
            String jwt = jwtUtils.generateJwtToken(authentication);

            // get UserDetails from Authentication object
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            // generate refresh token
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

            //response contains token type - Bearer, the JWT, refreshToken and user details
            map.put("message", "Login Successful!" );
            map.put("tokenType", "Bearer");
            map.put("accessToken", jwt);
            map.put("refreshToken", refreshToken.getToken());

            map.put("id", userDetails.getId());
            map.put("email", userDetails.getEmail());
            map.put("firstName", userDetails.getFirstName());
            map.put("lastName", userDetails.getLastName());
            map.put("roles", roles);

            return new ResponseEntity<>(map, HttpStatus.OK);

        } catch (Exception ex) {    // exception
            map.clear();
            map.put("message", "Login failed!" );
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/refreshtoken")
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();  // for holding response details

        String refreshTokenFromRequest = request.getRefreshToken(); // get the Refresh Token from request data

        // get the RefreshToken object {id, user, token, expiryDate} from raw Token using RefreshTokenService

        return refreshTokenService.findByToken(refreshTokenFromRequest)
                .map(refreshTokenService::verifyExpiration) // verify the token (expired or not) basing on expiryDate field
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromEmail(user.getEmail()); // generate new Access Token using JwtUtils

                    // response contains new accessToken, accessToken type and old refreshToken
                    map.put("accessToken", token);
                    map.put("refreshToken", refreshTokenFromRequest);
                    map.put("tokenType", "Bearer");
                    return new ResponseEntity<>(map, HttpStatus.OK);

                })
                .orElseThrow(() -> new TokenRefreshException(refreshTokenFromRequest,
                        "Refresh token is not in database!"));
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    public ResponseEntity<?> getRoles() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();  // for holding response details

        try {
            List<Role> roleList = roleRepository.findAll();

            if (!roleList.isEmpty()) {  // Roles found
                map.put("roles", roleList);
                return new ResponseEntity<>(map, HttpStatus.OK);

            } else {    // No roles found
                map.clear();
                map.put("message", "No roles found");
                return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
            }

        } catch (Exception ex) {    // Exception
            map.clear();
            map.put("message", ex.toString() );
            //map.put("message", "Oops! something went wrong");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    public ResponseEntity<?> logout() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();  // for holding response details

        // log out the user - remove user's refresh token from the database

        // Get the Principal object to check if the current user is authenticated or not
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principle.toString() != "anonymousUser") {
            Long userId = ((UserDetailsImpl) principle).getId();
            refreshTokenService.deleteByUserId(userId); // remove Refresh Token from the database
        }

        map.put("message", "You have been logged out!");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    @PostMapping("mock-login")
    public ResponseEntity<?> checkAuth(@Valid @RequestBody LoginRequest loginRequest) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();  // for holding response details

        try {
            // authenticate { email, password }
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            map.put("message", "Login Successful!" );
            return new ResponseEntity<>(map, HttpStatus.OK);

        } catch (Exception ex) {    // exception
            map.clear();
            map.put("message", "Login failed!" );
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
