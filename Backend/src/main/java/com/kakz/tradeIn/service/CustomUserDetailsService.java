package com.kakz.tradeIn.service;

import com.kakz.tradeIn.model.User;
import com.kakz.tradeIn.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * CustomUserDetailsService is an implementation of the UserDetailsService interface,
 * which is used to load user-specific data for authentication.
 * It retrieves user details from the database using the UserRepository.
 *
 * This service is marked as a Spring Service component.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Loads the user's details based on their username (email) for authentication purposes.
     *
     * @param username the email of the user trying to authenticate
     * @return UserDetails the user's details including username and password
     * @throws UsernameNotFoundException if the user with the given email is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if(user == null) {
            throw new UsernameNotFoundException("User does not found: "+username);
        }

        List<GrantedAuthority> authorityList = new ArrayList<>();

        return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(),
                authorityList);
    }
}
