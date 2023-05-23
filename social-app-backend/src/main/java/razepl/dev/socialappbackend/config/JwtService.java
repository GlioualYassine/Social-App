package razepl.dev.socialappbackend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import razepl.dev.socialappbackend.config.constants.Headers;
import razepl.dev.socialappbackend.config.constants.Properties;
import razepl.dev.socialappbackend.config.interfaces.JwtServiceInterface;
import razepl.dev.socialappbackend.exceptions.validators.NullChecker;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Service to help manage Jwt manipulation like creation.
 * It implements {@link JwtServiceInterface}.
 */
@Service
public class JwtService implements JwtServiceInterface {
    @Value(Properties.EXPIRATION_PROPERTY)
    private long expirationTime;

    @Value(Properties.ENCODING_KEY_PROPERTY)
    private String encodingKey;

    @Value(Properties.REFRESH_PROPERTY)
    private long refreshTime;

    @Override
    public final String getUsernameFromToken(String jwtToken) {
        NullChecker.throwIfNull(jwtToken);

        return getClaimFromToken(jwtToken, Claims::getSubject);
    }

    @Override
    public final <T> T getClaimFromToken(String jwtToken, Function<Claims, T> claimsHandler) {
        NullChecker.throwIfNull(jwtToken, claimsHandler);

        Claims claims = getAllClaims(jwtToken);

        return claimsHandler.apply(claims);
    }

    @Override
    public final String generateRefreshToken(UserDetails userDetails) {
        NullChecker.throwIfNull(userDetails);

        return buildToken(Collections.emptyMap(), userDetails, refreshTime);
    }

    @Override
    public final String generateToken(UserDetails userDetails) {
        NullChecker.throwIfNull(userDetails);

        return generateToken(Collections.emptyMap(), userDetails, expirationTime);
    }

    @Override
    public final String generateToken(Map<String, Object> additionalClaims, UserDetails userDetails, long expiration) {
        NullChecker.throwIfNull(additionalClaims, userDetails);

        return buildToken(additionalClaims, userDetails, expiration);
    }

    @Override
    public final boolean isTokenValid(String jwtToken, UserDetails userDetails) {
        NullChecker.throwIfNull(jwtToken, userDetails);

        String username = getUsernameFromToken(jwtToken);

        return username.equals(userDetails.getUsername()) && !isTokenExpired(jwtToken);
    }

    @Override
    public final String getJwtToken(HttpServletRequest request) {
        NullChecker.throwIfNull(request);

        String authHeader = request.getHeader(Headers.AUTH_HEADER);

        if (request.getServletPath().contains(Headers.AUTH_MAPPING) || authHeader == null || !authHeader.startsWith(Headers.TOKEN_HEADER)) {
            return null;
        }
        return authHeader.substring(Headers.TOKEN_START_INDEX);
    }

    @Override
    public final String getJwtRefreshToken(HttpServletRequest request) {
        NullChecker.throwIfNull(request);

        String authHeader = request.getHeader(Headers.AUTH_HEADER);

        if (authHeader == null || !authHeader.startsWith(Headers.TOKEN_HEADER)) {
            return null;
        }
        return authHeader.substring(Headers.TOKEN_START_INDEX);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(buildSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String buildToken(Map<String, Object> additionalClaims, UserDetails userDetails, long expiration) {
        long time = System.currentTimeMillis();

        return Jwts.builder()
                .setClaims(additionalClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(time))
                .setExpiration(new Date(time + expiration))
                .signWith(buildSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private Key buildSignInKey() {
        byte[] decodedKey = Decoders.BASE64.decode(encodingKey);

        return Keys.hmacShaKeyFor(decodedKey);
    }
}
