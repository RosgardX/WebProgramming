package back.auth;

import back.user.UserEntity;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET = "change-me-to-a-long-secret-change-me-to-a-long-secret";

    private static final long EXPIRATION_SECONDS = 24 * 60 * 60;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(UserEntity user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(EXPIRATION_SECONDS)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsername(String token) {
        return parse(token).getBody().getSubject();
    }

    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
    }
}