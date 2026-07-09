package com.microservices.userservice.command.controller;

import com.microservices.userservice.command.command.LoginCommand;
import com.microservices.userservice.command.handler.LoginCommandHandler;
import com.microservices.userservice.command.model.LoginRequestModel;
import com.microservices.userservice.infrastructure.identity.model.TokenExchangeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("api/v1/public")
@RequiredArgsConstructor
public class PublicCommandController {
    private final LoginCommandHandler loginCommandHandler;

    @PostMapping("/login")
    ResponseEntity<TokenExchangeResponse> login(@RequestBody LoginRequestModel model) {
        try {
            return ResponseEntity.ok(loginCommandHandler.handle(LoginCommand.from(model)));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
