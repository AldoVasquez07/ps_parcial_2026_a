package pruebas.software.parcial.service;
 
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pruebas.software.parcial.model.Usuario;
 
import java.util.Optional;

@Service
public class UsuarioService {
 
    private final UsuarioRepository repository;
 
    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    // Funcion que verifica si es que el nombre o email del Usuario no estan en blanco
    public boolean validarCamposObligatorios(String nombre, String email) {
        return StringUtils.isNotBlank(nombre) && StringUtils.isNotBlank(email);
    }

    
}