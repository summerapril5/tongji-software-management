package com.seme.wiseinvest.gateway.filter;

import com.seme.wiseinvest.common.utils.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private static final List<String> WHITELIST = Arrays.asList(
            "/account/login",
            "/account/account/create",
            "/product/product/list"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (isWhitelisted(path)) {
            return chain.filter(exchange);
        }

        HttpHeaders headers = request.getHeaders();
        String tokenHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);

        if (tokenHeader == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            String jwt = tokenHeader;

            // 使用parseToken 方法
            // 这个方法返回的是顶层 "claims" 声明内部的 Map
            Map<String, Object> innerClaimsMap = JwtUtil.parseToken(jwt);

            // 修改校验逻辑：fundAccount 或 adminAccount 任意一个存在即可
            boolean hasFundAccount = innerClaimsMap != null && innerClaimsMap.containsKey("fundAccount") && StringUtils.hasText((String) innerClaimsMap.get("fundAccount"));
            boolean hasAdminAccount = innerClaimsMap != null && innerClaimsMap.containsKey("adminAccount") && StringUtils.hasText((String) innerClaimsMap.get("adminAccount"));

            if (!hasFundAccount && !hasAdminAccount) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // 优先将 fundAccount 放入请求头，如果没有则放 adminAccount
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-Fund-Account", hasFundAccount ? (String) innerClaimsMap.get("fundAccount") : "")
                    .header("X-Admin-Account", hasAdminAccount ? (String) innerClaimsMap.get("adminAccount") : "")
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private boolean isWhitelisted(String path) {
        return WHITELIST.stream().anyMatch(path::startsWith);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}