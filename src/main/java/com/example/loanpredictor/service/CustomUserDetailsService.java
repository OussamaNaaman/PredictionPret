package com.example.loanpredictor.service;

import com.example.loanpredictor.entity.User;
import com.example.loanpredictor.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final  UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class); // ADDED LOGGER

    public CustomUserDetailsService (UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUserByUsername called for username: {}", username); // ADDED LOGGING - Line A
        Optional<User> user = userRepository.findByUsername(username); // Line B
        if(user.isEmpty()){                                         // Line C
            log.warn("User not found for username: {}", username); // ADDED LOGGING - User not found
            throw new UsernameNotFoundException("User does not exist");
        }
        log.info("User found: {}", user.get().getUsername()); // ADDED LOGGING - User found - Line D
        return user.get(); // Line E
    }

    @Transactional
    public void saveUser(User user){
        userRepository.save(user);
    }
}