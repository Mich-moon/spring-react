// Controller receives and handles request after it was filtered by OncePerRequestFilter.
// UserController has accessing protected resource methods with role based validations.

package com.demo.project2.controller;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import com.demo.project2.model.User;
import com.demo.project2.repository.UserRepository;


@CrossOrigin(origins = "http://localhost:3000") // to avoid CORS issues:
@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    public ResponseEntity<?> getUsers() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();  // for holding response details

        try {
            List<User> userList = userRepository.findAll();

            if (!userList.isEmpty()) {  // Users found
                map.put("users", userList);
                return new ResponseEntity<>(map, HttpStatus.OK);

            } else {    // No users found
                map.clear();
                map.put("message", "No users found");
                return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
            }

        } catch (Exception ex) {    // Exception
            map.clear();
            map.put("message", "Oops! something went wrong");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    public ResponseEntity<?>  getUser(@PathVariable Long id) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();  // for holding response details

        try {
            if (userRepository.findById(id).isPresent()) {  // User found
                User user = userRepository.findById(id).get();  // get value found
                map.put("user", user);
                return new ResponseEntity<>(map, HttpStatus.OK);

            } else {    // User not found
                map.clear();
                map.put("message", "User not found");
                return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
            }

        } catch (Exception ex) {    // Exception
            map.clear();
            map.put("message", "Oops! something went wrong");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();  // for holding response details

        try {
            userRepository.deleteById(id);
            map.put("message", "User deleted successfully");
            return new ResponseEntity<>(map, HttpStatus.OK);

        } catch (Exception ex) {    // Exception
            map.clear();
            map.put("message", "Oops! something went wrong");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> updateUser(@RequestBody User userUpdate, @PathVariable Long id) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();  // for holding response details

        try {
            if (userRepository.findById(id).isPresent()) {  // User found
                User currentUser = userRepository.findById(id).get(); // get value found

                // updating user details
                currentUser.setEmail(userUpdate.getEmail());
                currentUser.setFirstName(userUpdate.getFirstName());
                currentUser.setLastName(userUpdate.getLastName());
                currentUser.setPassword(encoder.encode( userUpdate.getPassword() ));

                // NB modify the roles that Hibernate is tracking
                currentUser.getRoles().clear();
                currentUser.getRoles().addAll(userUpdate.getRoles());
                //currentUser.setRoles(userUpdate.getRoles());

                userRepository.save(currentUser);   // save new details

                map.put("message", "User updated successfully");
                return new ResponseEntity<>(map, HttpStatus.OK);

            } else {    // User not found
                map.clear();
                map.put("message", "User not found");
                return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
            }

        } catch (Exception ex) {    // Exception
            map.clear();
            map.put("message", "Oops! something went wrong");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


}
