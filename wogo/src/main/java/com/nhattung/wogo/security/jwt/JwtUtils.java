package com.nhattung.wogo.security.jwt;

import com.nhattung.wogo.dto.request.LogoutRequestDTO;
import com.nhattung.wogo.entity.TokenBlacklist;
import com.nhattung.wogo.repository.RefreshTokenRepository;
import com.nhattung.wogo.repository.TokenBlacklistRepository;
import com.nhattung.wogo.security.user.WogoUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtUtils {  // create and validate JWT tokens

    @Value("${auth.token.jwtSecret}")
    private String jwtSecret;

    @Value("${auth.token.expirationInMils}")
    private int expirationTime;

    @Value("${auth.token.refreshExpirationInMils}")
    private int jwtRefreshExpiration;

    private final TokenBlacklistRepository tokenBlacklistRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    public String createAccessToken(Authentication authentication){
        WogoUserDetails userPrincipal = (WogoUserDetails) authentication.getPrincipal();

        List<String> roles = userPrincipal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .setSubject((userPrincipal.getPhone()))
                .claim("id", userPrincipal.getId())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expirationTime))
                .setId(UUID.randomUUID().toString())
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(Authentication authentication) {
        WogoUserDetails userPrincipal = (WogoUserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getPhone()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtRefreshExpiration))
                .setId(UUID.randomUUID().toString())
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }
    private Key key()
    {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }


    public  String getPhoneFromJwtToken(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException |
                 IllegalArgumentException e) {
            throw new JwtException(e.getMessage());
        }
    }

    public Claims verifyToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Transactional
    public void logout(LogoutRequestDTO request) {
        Claims claims = verifyToken(request.getToken());
        String jit = claims.getId();
        Date expriryDate = claims.getExpiration();

        // Blacklist Access Token
        tokenBlacklistRepository.save(TokenBlacklist.builder()
                .tokenJti(jit)
                .userId(claims.get("id", Long.class))
                .expiryDate(expriryDate)
                .build());

        //Revoke Refresh Token
        refreshTokenRepository.findByToken(request.getToken()).ifPresent(refreshToken -> {
            refreshToken.setRevoked(true);
            refreshToken.setExpiryDate(expriryDate);
            refreshTokenRepository.save(refreshToken);
        });
    }

    public boolean isTokenBlacklisted(String token) {
        Claims claims = verifyToken(token);
        String jti = claims.getId();
        return tokenBlacklistRepository.existsByTokenJti(jti);
    }

}
