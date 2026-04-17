package com.pragma.plazoleta.infrastructure.output.security.helper;

import com.pragma.plazoleta.domain.constants.DomainConstants;

import com.pragma.plazoleta.infrastructure.constants.InfrastructureConstants;
import com.pragma.plazoleta.infrastructure.output.security.adapter.TokenServiceAdapter;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;


@Component
@AllArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private final TokenServiceAdapter tokenServiceAdapter;
    private final ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        if (token == null) {
            filterChain.doFilter(request,response);
            return;
        }
        try{
            Claims claims = tokenServiceAdapter.validateToken(token);

            if (claims.getExpiration().before(new Date())){
                setResponseProperties(response, HttpServletResponse.SC_UNAUTHORIZED, InfrastructureConstants.MSG_TOKEN_EXPIRED, request);
                return;
            }

            SecurityContextHolder.getContext().setAuthentication(new JwtAuth(claims));
        }catch (Exception e){
            setResponseProperties(response, HttpServletResponse.SC_UNAUTHORIZED, InfrastructureConstants.MSG_TOKEN_INVALID, request);
            return;
        }
        filterChain.doFilter(request,response);
    }
    private void setResponseProperties(HttpServletResponse response, int status, String message, HttpServletRequest request) throws IOException {
        Map<String, Object> payload = new HashMap<>();
        payload.put(InfrastructureConstants.KEY_STATUS, status);
        payload.put(InfrastructureConstants.KEY_MESSAGE, message);
        payload.put(InfrastructureConstants.KEY_PATH, request != null ? request.getRequestURI() : InfrastructureConstants.EMPTY_STRING);

        response.setStatus(status);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(InfrastructureConstants.UTF_8);

        String json = objectMapper.writeValueAsString(payload);
        response.getWriter().write(json);
        response.getWriter().flush();
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(DomainConstants.TOKEN_PREFIX)) {
            return header.substring(7);
        }
        return null;
    }


}
