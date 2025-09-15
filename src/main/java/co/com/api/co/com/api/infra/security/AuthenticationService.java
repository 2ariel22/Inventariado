package co.com.api.co.com.api.infra.security;

import co.com.api.co.com.api.domain.usuarios.Usuario;
import co.com.api.co.com.api.domain.usuarios.UsuarioReposotiry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements UserDetailsService {
    @Autowired
    UsuarioReposotiry usuarioReposotiry;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        return usuarioReposotiry.findByUser(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }

}
