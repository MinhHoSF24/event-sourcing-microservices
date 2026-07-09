package com.microservices.userservice.command.handler;

import com.microservices.userservice.command.command.LoginCommand;
import com.microservices.userservice.infrastructure.identity.IdentityAccessService;
import com.microservices.userservice.infrastructure.identity.model.TokenExchangeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginCommandHandler {
    private final IdentityAccessService identityAccessService;

    public TokenExchangeResponse handle(LoginCommand command) {
        return identityAccessService.login(command);
    }
}
