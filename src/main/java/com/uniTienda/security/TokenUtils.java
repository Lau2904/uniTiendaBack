package com.uniTienda.security;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class TokenUtils {

    private final static String ACCESS_TOKEN_SECRET = "4qhq8LrEBfYcaRHxhdb9zURb2rf8e7Ud";
    private final static Long ACCESS_TOKEN_VALIDITY_SECONDS = 2_592_000L;

    public static String createToken(String nombre, String email){
        Long expirationTime = ACCESS_TOKEN_VALIDITY_SECONDS * 1_000;
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);

        Map<String, Object> extra = new HashMap<>();
        extra.put("nombre", nombre);

        return Jwts.builder()
                .setSubject(email)
                .setExpiration(expirationDate)
                .addClaims(extra)
                .signWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes()))
                .compact();
    }

    public static UsernamePasswordAuthenticationToken getetAuthentication(String token){
        try{
            Claims claims = Jwts.parserBuilder()
            .setSigningKey(ACCESS_TOKEN_SECRET.getBytes()) 
            .build()
            .parseClaimsJws(token)
            .getBody();

            String email = claims.getSubject();

            return  new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
        }catch (JwtException e){
            return null;
        }
    }

}
