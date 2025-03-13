package com.thingwire.iot.service;

import com.thingwire.iot.entity.User;

public interface AuthenticationService {
    String registerUser(String username, String password, User.Role role);
    String authenticateUser(String username, String password);

}
