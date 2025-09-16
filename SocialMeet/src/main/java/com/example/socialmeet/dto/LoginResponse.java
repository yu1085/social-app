package com.example.socialmeet.dto;

import lombok.Data;

@Data
public class LoginResponse {
    
    private String token;
    private UserDTO user;
    
    public LoginResponse(String token, UserDTO user) {
        this.token = token;
        this.user = user;
    }
}
