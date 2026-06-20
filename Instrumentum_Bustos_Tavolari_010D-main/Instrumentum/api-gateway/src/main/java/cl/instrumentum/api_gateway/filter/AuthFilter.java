package cl.instrumentum.api_gateway.filter;

import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config>{

    @Value("${jwt.secret}")
    private String secreto;

    public AuthFilter() {
        super(Config.class);
    }

    public static class Config{
        // Configuración adicional si fuera necesaria
    }

    @Override
    public GatewayFilter apply(Config config){
        return (exchange, chain) ->{
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Token faltante o formato inválido", HttpStatus.UNAUTHORIZED);
            }
            
            String token = authHeader.substring(7);
            try{
                Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token);
            } catch (Exception e) {
                return onError(exchange, "Token inválido o expirado", HttpStatus.UNAUTHORIZED);
            }
            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus){
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }
}