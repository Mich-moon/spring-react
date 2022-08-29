// for getting user details object

package com.demo.project2.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.project2.model.User;
import com.demo.project2.repository.UserRepository;

public class UserDetailsServiceImpl implements UserDetailsService {

    // UserDetailsService interface has a method to load User by username/email and returns
    // a UserDetails object that Spring Security can use for authentication and validation.

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username/email: " + email));

        return UserDetailsImpl.build(user);
    }
}
