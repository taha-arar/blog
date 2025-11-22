package com.blog.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

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
                 .signWith(getSigningKey(), SignatureAlgorithm.ES256)
                 .compact();

    }

    private SecretKey getSigningKey(){
        byte [] keyBytes = jwtProperties.getSecret().getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
