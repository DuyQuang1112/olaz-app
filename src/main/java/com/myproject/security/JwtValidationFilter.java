package com.myproject.security;

import com.myproject.repository.InvalidTokenRepository;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtValidationFilter extends OncePerRequestFilter {

//    private static final Dotenv dotenv = Dotenv.load();
//    private final String SIGNER_KEY = dotenv.get("JWT_SIGNER_KEY");

    @Value("${api_signer_key}")
    private String SIGNER_KEY;

    private final InvalidTokenRepository invalidTokenRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        // Exclude certain endpoints
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/login") || requestURI.equals("/register")) {
            filterChain.doFilter(request, response); // Skip validation for these endpoints
            return;
        }

        if (token != null) {
            try {
                String jwtId = Jwts.parserBuilder()
                        .setSigningKey(SIGNER_KEY.getBytes())
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .getId();

                if (invalidTokenRepository.findById(jwtId).isPresent()) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid token");
                    return;
                }

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
