package com.microservices.apigateway.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

class SecurityConfigTest {

    @Test
    void keycloakAuthoritiesConverterReadsScopesRealmRolesAndClientRoles() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("scope", "openid profile")
                .claim("realm_access", Map.of("roles", List.of("user")))
                .claim("resource_access", Map.of("library-app", Map.of("roles", List.of("librarian"))))
                .build();

        List<String> authorities = new SecurityConfig.KeycloakAuthoritiesConverter()
                .convert(jwt)
                .map(GrantedAuthority::getAuthority)
                .collectList()
                .block();

        assertThat(authorities)
                .contains("SCOPE_OPENID", "SCOPE_PROFILE", "ROLE_USER", "ROLE_LIBRARIAN");
    }
}
