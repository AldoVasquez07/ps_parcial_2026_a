package pruebas.software.parcial.repository;
 
import pruebas.software.parcial.model.Usuario;
import java.util.Optional;
 

public interface UsuarioRepository {
    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByEmail(String email);
    Usuario save(Usuario usuario);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
 