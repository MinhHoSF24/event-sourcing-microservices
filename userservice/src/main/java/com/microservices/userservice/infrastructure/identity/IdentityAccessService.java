package com.microservices.userservice.infrastructure.identity;

import com.microservices.userservice.command.command.LoginCommand;
import com.microservices.userservice.domain.model.UserRegistrationProfile;
import com.microservices.userservice.infrastructure.identity.model.Credential;
import com.microservices.userservice.infrastructure.identity.model.TokenExchangeResponse;
import com.microservices.userservice.infrastructure.identity.model.UserCreationParam;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IdentityAccessService {
    private final IdentityClient identityClient;

    @Value("${idp.client-id}")
    @NonFinal
    String clientId;

    @Value("${idp.client-secret}")
    @NonFinal
    String clientSecret;

    public String createIdentityUser(UserRegistrationProfile profile) {
        TokenExchangeResponse token = identityClient.exchangeClientToken(clientCredentialsForm());
        ResponseEntity<?> creationResponse = identityClient.createUser(UserCreationParam.builder()
                .username(profile.username())
                .firstName(profile.firstName())
                .lastName(profile.lastName())
                .email(profile.email())
                .enabled(true)
                .emailVerified(false)
                .credentials(List.of(Credential.builder()
                        .type("password")
                        .temporary(false)
                        .value(profile.password())
                        .build()))
                .build(), "Bearer " + token.getAccessToken());
        return extractUserId(creationResponse);
    }

    public TokenExchangeResponse login(LoginCommand command) {
        try {
            return identityClient.exchangeUserToken(passwordGrantForm(command));
        } catch (FeignException.BadRequest | FeignException.Unauthorized ex) {
            // Keycloak returns 400 invalid_grant for wrong username/password.
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
    }

    private MultiValueMap<String, String> clientCredentialsForm() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("scope", "openid");
        return form;
    }

    private MultiValueMap<String, String> passwordGrantForm(LoginCommand command) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("scope", "openid");
        form.add("username", command.username());
        form.add("password", command.password());
        return form;
    }

    private String extractUserId(ResponseEntity<?> response) {
        List<String> locations = response.getHeaders().get("Location");
        if (locations == null || locations.isEmpty()) {
            throw new IllegalStateException("Location header is missing in the response");
        }

        String location = locations.get(0);
        String[] splitedStr = location.split("/");
        return splitedStr[splitedStr.length - 1];
    }
}
