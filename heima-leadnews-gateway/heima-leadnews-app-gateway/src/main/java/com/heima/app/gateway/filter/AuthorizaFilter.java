package com.heima.app.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.heima.app.gateway.util.AppJwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthorizaFilter implements Ordered, GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //先获取request 和 response对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //判断是否是登陆
        if (request.getURI().getPath().contains("/login")){
            return chain.filter(exchange);
        }
        //获取token
        String token = request.getHeaders().getFirst("token");
        //判断是否存在
        if (StrUtil.isBlank(token)){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        try {
            //判断有效
            Claims claimsBody = AppJwtUtil.getClaimsBody(token);
            //是否过期
            int verifyToken = AppJwtUtil.verifyToken(claimsBody);

            if (verifyToken == 1 || verifyToken == 2){
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chain.filter(exchange);
    }

    /**
     * 优先级设置 值小  优先级越高
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
