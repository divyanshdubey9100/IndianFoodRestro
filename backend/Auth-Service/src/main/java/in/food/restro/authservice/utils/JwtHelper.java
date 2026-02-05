package in.food.restro.authservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import in.food.restro.authservice.dtos.RoleDto;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtHelper {

    // 1. Secret Key: In production, move this to application.properties!
    // It must be at least 256-bit (32 characters long) for HS256.
    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    // 2. Token Validity: 5 Hours (in milliseconds)
    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60 * 1000;

    /**
     * Generates a token for a specific username
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        log.debug("JwtHelper: Generating token for user: {}", subject);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String generateToken(String username, Set<RoleDto> roles) {
        Map<String, Object> claims = new HashMap<>();
        
        // Extract just the role names to keep the token small
        Set<String> roleNames = roles.stream()
                                     .map(RoleDto::getName) // Assuming your DTO has getRoleName()
                                     .collect(Collectors.toSet());
                                     
        claims.put("roles", roleNames);
        return createToken(claims, username);
    }

    /**
     * Decode the secret key
     */
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // --- Utility Methods to Validate/Parse Token ---

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = getUsernameFromToken(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}