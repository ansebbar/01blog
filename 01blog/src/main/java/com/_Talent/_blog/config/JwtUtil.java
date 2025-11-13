package com._Talent._blog.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret:mySecretKey123456789012345678901234567890}")
    private String secret;
    
    private Key getSignKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    public String getUsername(String token) {
        return getClaim(token, Claims::getSubject);
    }
    
    public Date getExpdate(String token) {
        return getClaim(token, Claims::getExpiration);
    }
    
    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("JWT token has expired", e);
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }
    
    private Boolean TkExpired(String token) {
        try {
            return getExpdate(token).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
    
    public String TkGenerate(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return TkCreate(claims, userDetails.getUsername());
    }
    
    private String TkCreate(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public Boolean TkCheck(String token, UserDetails userDetails) {
        try {
            final String username = getUsername(token);
            return (username.equals(userDetails.getUsername()) && !TkExpired(token));
        } catch (Exception e) {
            return false;
        }
    }
}