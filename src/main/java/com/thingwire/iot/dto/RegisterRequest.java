package com.thingwire.iot.dto;

import com.thingwire.iot.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String username;
    private String password;
    private User.Role role;
}
