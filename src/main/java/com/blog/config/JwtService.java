package com.blog.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    public  String generateToken(UserDetails userDetails){
        Map<String, Object> claims =  Map.of(
                "roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                        .toList()
        );
        return generateToken(claims, userDetails);
    }

    public String generateToken(Map<String, Object> claims, UserDetails userDetails){
         Instant now = Instant.now();
         Instant expiration = now.plus(jwtProperties.getExpirationMinutes(), ChronoUnit.MINUTES);

         return Jwts.builder()
                 .setClaims(claims)
                 .setSubject(userDetails.getUsername())
                 .setIssuedAt(Date.from(now))
                 .setExpiration(Date.from(expiration))
                 .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                 .compact();

    }

    private SecretKey getSigningKey(){
        byte [] keyBytes = jwtProperties.getSecret().getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return  extract(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extract(token, Claims::getExpiration);
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }



    public <T> T extract(String token, Function<Claims,T> claimsTResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsTResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
