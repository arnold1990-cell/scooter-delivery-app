package com.scooter.app.shared.jwt;

import com.scooter.app.modules.iam.User;
import com.scooter.app.modules.iam.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final UserRepository userRepository;
    private final JwtRoleMapper jwtRoleMapper;

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-minutes}")
    private long expirationMinutes;

    public String generateToken(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found for JWT generation"));

        List<String> authorities = userDetails.getAuthorities().stream()
                .map(Object::toString)
                .toList();

        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + expirationMinutes * 60_000);

        log.debug("Generating JWT for user={} with authoritiesClaim={}", userDetails.getUsername(), authorities);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("authorities", authorities)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public List<String> extractAuthorities(String token) {
        Claims claims = extractAllClaims(token);
        Object rawAuthorities = claims.get("authorities");

        if (rawAuthorities instanceof List<?> authorities) {
            return authorities.stream()
                    .map(String::valueOf)
                    .map(jwtRoleMapper::toAuthority)
                    .map(String::toUpperCase)
                    .toList();
        }

        // Backward compatibility with older tokens.
        Object rawRoles = claims.get("roles");
        if (rawRoles instanceof List<?> roles) {
            return roles.stream()
                    .map(String::valueOf)
                    .map(jwtRoleMapper::toAuthority)
                    .map(String::toUpperCase)
                    .toList();
        }

        return List.of();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
