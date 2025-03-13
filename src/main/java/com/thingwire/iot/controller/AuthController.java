package com.thingwire.iot.controller;

import com.thingwire.iot.dto.LoginRequest;
import com.thingwire.iot.dto.RegisterRequest;
import com.thingwire.iot.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authenticationService.registerUser(request.getUsername(), request.getPassword(), request.getRole());
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return authenticationService.authenticateUser(request.getUsername(), request.getPassword());
    }
}
