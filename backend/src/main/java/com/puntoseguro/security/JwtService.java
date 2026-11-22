package com.puntoseguro.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import com.puntoseguro.entity.enums.UserRole;
import com.puntoseguro.repository.UserRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final SecretKey jwtSigningKey;
    private final JwtParser jwtParser;
    private final JwtProperties properties;
    private final Clock jwtClock;
    private final UserRepository users;

    public String issue(UUID userId, UserRole role) {
        Instant now = jwtClock.instant();
        return Jwts.builder().subject(userId.toString())
                .claim("role", role.name())
                .claim("version", users.findById(userId).orElseThrow(() -> new JwtException("Usuario no válido.")).getSecurityVersion())
                .issuer(properties.getIssuer()).audience().add(properties.getAudience()).and()
                .issuedAt(Date.from(now)).expiration(Date.from(now.plusSeconds(properties.getAccessTokenTtlSeconds())))
                .id(UUID.randomUUID().toString()).signWith(jwtSigningKey, Jwts.SIG.HS256).compact();
    }

    public PuntoSeguroPrincipal validate(String token) {
        Claims claims = jwtParser.parseSignedClaims(token).getPayload();
        if (claims.getSubject() == null || claims.getExpiration() == null || claims.getIssuedAt() == null
                || !claims.getExpiration().toInstant().isAfter(jwtClock.instant())
                || claims.getIssuedAt().after(Date.from(jwtClock.instant()))
                || !claims.getExpiration().after(claims.getIssuedAt())) {
            throw new JwtException("Token no válido.");
        }
        UUID userId = UUID.fromString(claims.getSubject());
        if (!userId.toString().equals(claims.getSubject())) {
            throw new JwtException("Token no válido.");
        }
        String claimRole = claims.get("role", String.class);
        if (claimRole == null) throw new JwtException("Token sin rol.");
        UserRole role = UserRole.valueOf(claimRole);
        Number version=claims.get("version", Number.class);
        if (version == null || users.findById(userId).filter(u -> u.isActivo() && u.getRole()==role && u.getSecurityVersion()==version.longValue()).isEmpty()) {
            throw new JwtException("Sesión revocada.");
        }
        return new PuntoSeguroPrincipal(userId, role);
    }

    public int expiresIn() {
        return properties.getAccessTokenTtlSeconds();
    }
}
