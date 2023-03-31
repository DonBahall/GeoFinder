package com.example.geofinder.Service;

import com.example.geofinder.Model.UserDto;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    void createUser(String emailR,String passwordR);
    UserDetails findUser(String email, String password);
}
