package com.example.geofinder.Service;

import com.example.geofinder.Model.UserModel;
import com.example.geofinder.Repository.UserRepository;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserModel user = userRepository.findByEmail(email).orElseThrow(()
                -> new UsernameNotFoundException("Invalid email or password"));
        return new org.springframework.security.core.userdetails.User(user.getEmail()
                , user.getPassword(), List.of(new SimpleGrantedAuthority(user.getRole().name())));

    }
}
