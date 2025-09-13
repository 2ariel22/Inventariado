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

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    private TokenGenerate tokenGenerate;

    @Autowired
    private UsuarioReposotiry usuarioReposotiry;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String Auth = request.getHeader("Authorization");
        if(Auth!=null){
            Auth = Auth.replace("Bearer ", "");
            if(Auth != ""){
                UserDetails usuario = usuarioReposotiry.findByUser(tokenGenerate.getSujet(Auth));
                var Authenticado = new UsernamePasswordAuthenticationToken(usuario,null,usuario.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(Authenticado);
            }
        }
        filterChain.doFilter(request,response);
    }
}
