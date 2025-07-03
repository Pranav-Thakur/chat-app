package com.chatapp.config;

import com.chatapp.exception.ChatAppException;
import com.chatapp.exception.ErrorCodes;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("${chatapp.jwt_token.secret_key}")
    private String SECRET_KEY;

    public String generateToken(String username, long expiryInMillis) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() +  expiryInMillis))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractSubjectForFilter(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public String extractSubject(String token) {
        try {
            return extractSubjectForFilter(token);
        } catch (ExpiredJwtException e) {
            throw new ChatAppException("auth token expired", ErrorCodes.EXPIRY_AUTH_TOKEN, HttpStatus.UNAUTHORIZED);
        } catch (JwtException e) {
            throw new ChatAppException("auth token unauthorised", ErrorCodes.UNAUTH_AUTH_TOKEN, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            throw new ChatAppException("auth token not valid", ErrorCodes.INVALID_AUTH_TOKEN, HttpStatus.UNAUTHORIZED);
        }
    }

    public Pair<Boolean, Object> extractSubjectAndCheckIfValidEvenIfExpired(String token) {
        Claims claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return Pair.of(false, claims.getSubject());
        } catch (ExpiredJwtException e) {
            // 👍 recover sessionId
            claims = e.getClaims();

            // check for validity, this is app and use specific as my case it should be a valid UUID, so no IllegalArgumentException
            return Pair.of(true, UUID.fromString(claims.getSubject()));
        } catch (JwtException e) {
            throw new ChatAppException("auth token unauthorised", ErrorCodes.UNAUTH_AUTH_TOKEN, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            throw new ChatAppException("auth token not valid", ErrorCodes.INVALID_AUTH_TOKEN, HttpStatus.UNAUTHORIZED);
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY.getBytes())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
