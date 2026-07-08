package com.microservices.userservice.controllers;


import com.microservices.userservice.dto.LoginRequestDTO;
import com.microservices.userservice.dto.identities.TokenExchangeResponse;
import com.microservices.userservice.services.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/public")
public class PublicController {
    private final com.microservices.userservice.services.IUserService userService;

    public PublicController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    ResponseEntity<TokenExchangeResponse> login(@RequestBody LoginRequestDTO dto){
        return ResponseEntity.ok(userService.login(dto));
    }
}