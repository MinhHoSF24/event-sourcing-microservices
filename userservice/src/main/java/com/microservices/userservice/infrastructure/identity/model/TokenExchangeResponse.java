package com.microservices.userservice.infrastructure.identity.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenExchangeResponse {
    String accessToken;
    String refreshToken;
    Long expiresIn;
    Long refreshExpiresIn;
    String tokenType;
    String idToken;
    Long notBeforePolicy;
    String sessionState;
    String scope;
}
