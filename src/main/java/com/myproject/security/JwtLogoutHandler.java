package com.myproject.security;

import com.myproject.model.InvalidatedToken;
import com.myproject.repository.InvalidTokenRepository;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class JwtLogoutHandler implements LogoutHandler {

    //for local
//    private static final Dotenv dotenv = Dotenv.load();
//    private final String SIGNER_KEY = dotenv.get("JWT_SIGNER_KEY");

    @Value("${api_signer_key}")
    private String SIGNER_KEY;

    private final InvalidTokenRepository invalidTokenRepository;

    public JwtLogoutHandler(InvalidTokenRepository invalidTokenRepository) {
        this.invalidTokenRepository = invalidTokenRepository;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = resolveToken(request);

        if (token != null) {
            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(SIGNER_KEY.getBytes())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                String jwtId = claims.getId();
                Date expiration = claims.getExpiration();

                // Save the invalidated token with its expiration time
                InvalidatedToken invalidatedToken = new InvalidatedToken();
                invalidatedToken.setId(jwtId);
                invalidatedToken.setExpiryTime(expiration);
                invalidTokenRepository.save(invalidatedToken);

            } catch (Exception e) {
                System.err.println("Handle token error: " + e.getMessage());
            }
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
