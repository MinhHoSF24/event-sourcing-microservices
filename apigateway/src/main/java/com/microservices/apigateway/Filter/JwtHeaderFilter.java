package com.microservices.apigateway.Filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class JwtHeaderFilter  extends AbstractGatewayFilterFactory<JwtHeaderFilter.Config> {

    public JwtHeaderFilter(){
        super(JwtHeaderFilter.Config.class);
    }

    @Override
    public GatewayFilter apply(JwtHeaderFilter.Config config) {
        return (exchange, chain) -> {
            return exchange.getPrincipal().flatMap(principal -> {
                if(principal instanceof JwtAuthenticationToken jwtAuth){
                    var claims = jwtAuth.getToken().getClaims();
                    var userId = String.valueOf(claims.get("sub"));
                    var userName = String.valueOf(claims.getOrDefault("preferred_username", userId));
                    var roles = jwtAuth.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .filter(authority -> authority.startsWith("ROLE_"))
                            .map(authority -> authority.substring("ROLE_".length()))
                            .sorted()
                            .toList();
                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            // Strip spoofable identity headers before adding gateway-verified values.
                            .headers(headers -> {
                                headers.remove("X-User-Id");
                                headers.remove("X-Username");
                                headers.remove("X-User-Roles");
                            })
                            .header("X-User-Id",userId)
                            .header("X-Username",userName)
                            .header("X-User-Roles", String.join(",", roles)).build();

                    ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
                    return chain.filter(mutatedExchange);
                }
                return chain.filter(exchange);
            }).switchIfEmpty(chain.filter(stripIdentityHeaders(exchange)));
        };
    }

    private ServerWebExchange stripIdentityHeaders(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(headers -> {
                    headers.remove("X-User-Id");
                    headers.remove("X-Username");
                    headers.remove("X-User-Roles");
                })
                .build();
        return exchange.mutate().request(request).build();
    }

    static class Config{

    }
}
