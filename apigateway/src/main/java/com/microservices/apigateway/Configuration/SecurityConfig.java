package com.microservices.apigateway.Configuration;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.util.StringUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {
    private static final String[] USER_OR_STAFF = { "ROLE_USER", "ROLE_LIBRARIAN", "ROLE_ADMIN" };
    private static final String[] STAFF = { "ROLE_LIBRARIAN", "ROLE_ADMIN" };

    @Bean
    SecurityWebFilterChain filterChain(ServerHttpSecurity http) throws Exception {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/public/login").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/books/**").hasAnyAuthority(USER_OR_STAFF)
                        .pathMatchers(HttpMethod.POST, "/api/v1/books/**").hasAnyAuthority(STAFF)
                        .pathMatchers(HttpMethod.PUT, "/api/v1/books/**").hasAnyAuthority(STAFF)
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/books/**").hasAnyAuthority(STAFF)
                        .pathMatchers("/api/v1/employees/**").hasAnyAuthority(STAFF)
                        .pathMatchers(HttpMethod.POST, "/api/v1/borrowings/**").hasAnyAuthority(USER_OR_STAFF)
                        .pathMatchers(HttpMethod.PUT, "/api/v1/borrowings/*/return").hasAnyAuthority(USER_OR_STAFF)
                        .pathMatchers(HttpMethod.GET, "/api/v1/borrowings/**").hasAnyAuthority(STAFF)
                        .pathMatchers("/api/v1/users/**").hasAuthority("ROLE_ADMIN")
                        .anyExchange().denyAll())
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    /**
     * Converter to extract authorities from JWT token.
     * It collects roles and scopes from the token and converts them into Spring
     * Security authorities.
     */
    @Bean
    Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakAuthoritiesConverter());
        return converter;
    }

    /**
     * Custom converter to extract roles and scopes from Keycloak JWT token.
     * It handles the extraction of roles from realm_access and resource_access
     * claims,
     * as well as scopes from scope and scp claims.
     */
    static class KeycloakAuthoritiesConverter implements Converter<Jwt, Flux<GrantedAuthority>> {
        @Override
        public Flux<GrantedAuthority> convert(Jwt jwt) {
            Set<String> authorities = new HashSet<>();
            collectScopes(jwt.getClaims().get("scope"), authorities);
            collectScopes(jwt.getClaims().get("scp"), authorities);
            collectRoles(jwt.getClaim("realm_access"), authorities);
            collectClientRoles(jwt.getClaim("resource_access"), authorities);

            return Flux.fromIterable(authorities.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList());
        }

        private void collectScopes(Object claim, Set<String> authorities) {
            if (claim instanceof String scopes) {
                for (String scope : scopes.split(" ")) {
                    addAuthority(authorities, "SCOPE_", scope);
                }
            } else if (claim instanceof Collection<?> scopes) {
                scopes.forEach(scope -> addAuthority(authorities, "SCOPE_", String.valueOf(scope)));
            }
        }

        private void collectRoles(Object roleContainer, Set<String> authorities) {
            if (roleContainer instanceof Map<?, ?> roleMap && roleMap.get("roles") instanceof Collection<?> roles) {
                roles.forEach(role -> addAuthority(authorities, "ROLE_", String.valueOf(role)));
            }
        }

        private void collectClientRoles(Object resourceAccess, Set<String> authorities) {
            if (!(resourceAccess instanceof Map<?, ?> clients)) {
                return;
            }
            // Keycloak stores client roles under resource_access.{client-id}.roles.
            clients.values().forEach(client -> collectRoles(client, authorities));
        }

        private void addAuthority(Set<String> authorities, String prefix, String value) {
            if (!StringUtils.hasText(value)) {
                return;
            }
            authorities.add(prefix + value.trim().toUpperCase(Locale.ROOT).replace('-', '_'));
        }
    }
}
