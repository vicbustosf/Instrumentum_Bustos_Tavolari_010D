package cl.instrumentum.service_auth.service;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    
    @Value("${jwt.secret}")
    private String secreto;

    public String generarToken(String username, List<String> roles){
        long dosHorasEnMilisegundos = 1000*60*60*2;

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + dosHorasEnMilisegundos))
                .signWith(Keys.hmacShaKeyFor(secreto.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }
}

/* Entonces, resumiendo,
Cuando la clase JwtService ejecuta el método .signWith(..., SignatureAlgorithm.HS256), ocurre lo siguiente:

1.- El algoritmo toma la primera parte del token (el Header)
2.- Toma la segunda parte del token (el Payload, donde está el username y los roles).
3.- Toma la Llave Secreta del servidor.
4.- Mezcla estos tres elementos y los pasa por la "licuadora" del HS256.

El resultado matemático de esa mezcla es la Firma (Signature), que se convierte en la tercera y última parte del token */