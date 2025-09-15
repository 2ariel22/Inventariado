package co.com.api.co.com.api.domain.usuarios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioReposotiry extends JpaRepository<Usuario,Long> {
    Optional<Usuario> findByUser(String username);

    Optional<Usuario> findByEmail(String email);
}
