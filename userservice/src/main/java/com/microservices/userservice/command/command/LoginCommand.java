package com.microservices.userservice.command.command;

import com.microservices.userservice.command.model.LoginRequestModel;
import org.springframework.util.StringUtils;

public record LoginCommand(String username, String password) {
    public LoginCommand {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new IllegalArgumentException("Username and password are required");
        }
        username = username.trim();
    }

    public static LoginCommand from(LoginRequestModel model) {
        if (model == null) {
            throw new IllegalArgumentException("Username and password are required");
        }
        return new LoginCommand(model.getUsername(), model.getPassword());
    }
}
