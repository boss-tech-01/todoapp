package com.myproject.todoapp.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {

    private final Integer accessTokenExpiration;
    private final Integer refreshTokenExpiration;
    private final SecretKey secretKey;

    public JwtUtils(@Value("${app.jwt.access-token-ttl-min}") Integer accessTokenExpiration,
                    @Value("${app.jwt.refresh-token-ttl-day}") Integer refreshTokenExpiration,
                    @Value("${app.jwt.secret-key}") String  secretKey) {
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String username){
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(accessTokenExpiration, ChronoUnit.MINUTES)))
                .addClaims(Map.of("type", "access"))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public String generateRefreshToken(String username){
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(refreshTokenExpiration, ChronoUnit.DAYS)))
                .addClaims(Map.of("type", "refresh"))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public boolean validateAccessToken(String token){
        try {
            Claims claims = parse(token);
            return claims.get("type", String.class).equals("access") &&
                    claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean validateRefreshToken(String token){
        try {
            Claims claims = parse(token);
            return claims.get("type", String.class).equals("refresh") &&
                    claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractUsernameFromToken(String token){
        return parse(token).getSubject();
    }

    //Private methods
    private Claims parse(String token){
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(removeBearerFromToken(token))
                .getBody();
    }

    private String removeBearerFromToken(String token){
        if (token == null) return null;
        return token.startsWith("Bearer ") ? token.substring(7) : token;
    }
}
