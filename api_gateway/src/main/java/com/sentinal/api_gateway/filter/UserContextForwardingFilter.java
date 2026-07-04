package com.sentinal.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class UserContextForwardingFilter implements GlobalFilter, Ordered
{

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain)
    {
        return exchange.getPrincipal()
                .cast(JwtAuthenticationToken.class)
                .map(authentication -> {
                    String userId = claimAsString(authentication, "userId");
                    String email = authentication.getToken().getSubject();
                    String role = claimAsString(authentication, "role");

                    ServerHttpRequest request = (ServerHttpRequest) exchange.getRequest()
                            .mutate()
                            .headers(headers -> {
                                headers.remove("X-User-Id");
                                headers.remove("X-User-Email");
                                headers.remove("X-User-Role");

                                if(userId != null)
                                {
                                    headers.add("X-User-Id", userId);
                                }
                                if(email != null)
                                {
                                    headers.add("X-User-Email", email);
                                }
                                if(role != null)
                                {
                                    headers.add("X-User-Role", role);
                                }
                            })
                            .build();
                    return exchange.mutate()
                            .request(request)
                            .build();
                })
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }

    private String claimAsString(JwtAuthenticationToken authentication, String claimName)
    {
        Object value = authentication.getToken().getClaims().get(claimName);
        return value != null ? String.valueOf(value) : null;
    }

    @Override
    public int getOrder()
    {
        return 0;
    }
}
