package com.thingwire.iot.service;

import com.thingwire.iot.entity.User;
import com.thingwire.iot.repository.UserRepository;
import com.thingwire.iot.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String registerUser(String username, String password, User.Role role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();

        userRepository.save(user);

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role.name());

        return jwtUtil.generateToken(username, claims);
    }

    public String authenticateUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                Map<String, Object> claims = new HashMap<>();
                claims.put("role", user.getRole().name());

                return jwtUtil.generateToken(username, claims);
            }
        }
        throw new RuntimeException("Invalid username or password");
    }
}
