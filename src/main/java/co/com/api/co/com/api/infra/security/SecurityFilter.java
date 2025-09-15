package co.com.api.co.com.api.infra.security;


import co.com.api.co.com.api.domain.usuarios.UsuarioReposotiry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);
    
    @Autowired
    private TokenGenerate tokenGenerate;

    @Autowired
    private UsuarioReposotiry usuarioReposotiry;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String Auth = request.getHeader("Authorization");
        String requestURI = request.getRequestURI();
        
        logger.info("SecurityFilter - Request URI: {}", requestURI);
        logger.info("SecurityFilter - Authorization header: {}", Auth != null ? "Present" : "Missing");
        
        // Solo procesar token si existe y no es un endpoint de autenticación
        if(Auth != null && !Auth.isEmpty() && !requestURI.startsWith("/api/auth/")){
            Auth = Auth.replace("Bearer ", "");
            if(!Auth.isEmpty()){
                try {
                    logger.info("SecurityFilter - Processing token for URI: {}", requestURI);
                    String username = tokenGenerate.getSujet(Auth);
                    logger.info("SecurityFilter - Extracted username: {}", username);
                    
                    UserDetails usuario = usuarioReposotiry.findByUser(username)
                            .orElse(null);
                    if (usuario != null) {
                        logger.info("SecurityFilter - User found: {}, authorities: {}", usuario.getUsername(), usuario.getAuthorities());
                        var Authenticado = new UsernamePasswordAuthenticationToken(usuario,null,usuario.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(Authenticado);
                        logger.info("SecurityFilter - Authentication set successfully");
                    } else {
                        logger.warn("SecurityFilter - User not found: {}", username);
                    }
                } catch (Exception e) {
                    logger.error("SecurityFilter - Error processing token: {}", e.getMessage());
                }
            }
        } else {
            logger.info("SecurityFilter - Skipping token processing for URI: {}", requestURI);
        }
        filterChain.doFilter(request,response);
    }
}
