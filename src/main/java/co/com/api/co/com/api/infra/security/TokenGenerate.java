package co.com.api.co.com.api.infra.security;

import co.com.api.co.com.api.domain.usuarios.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
public class TokenGenerate {
    @Value("${api.security.secret}")
    private String valorSecret;

    public String generarToken(Usuario usuario){
        try {
            Algorithm algorithm = Algorithm.HMAC256(valorSecret);
            return JWT.create()
                    .withIssuer("inventory")
                    .withSubject(usuario.getUser())
                    .withClaim("id", usuario.getId())
                    .withExpiresAt(generarFechaExpiracion())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new RuntimeException();
        }
    }
    public String getSujet(String Token){
        if(Token == null){
            throw new RuntimeException("TOken nulo");
        }
        String user = "";
        try {
            Algorithm algorithm = Algorithm.HMAC256(valorSecret);
            DecodedJWT verifier = JWT.require(algorithm)
                    .withIssuer("inventory")
                    .build().verify(Token);

            user = verifier.getSubject();
        } catch (JWTVerificationException exception){
            // Invalid signature/claims
        }
        if(user == "" || user == null){
            throw new RuntimeException("user no valido");
        }
        return user;
    }

    private Instant generarFechaExpiracion() {
        return LocalDateTime.now().plusHours(3).toInstant(ZoneOffset.of("-05:00"));
    }
}
