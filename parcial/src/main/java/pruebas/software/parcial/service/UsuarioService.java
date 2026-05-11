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

    // Metodo que verifica si es que el nombre o email del Usuario no estan en blanco
    public boolean validarCamposObligatorios(String nombre, String email) {
        return StringUtils.isNotBlank(nombre) && StringUtils.isNotBlank(email);
    }

    // Metodo que devuelve el nombre del usuario normalizado en caso de que no este en blanco
    public String normalizarNombre(String nombre) {
        return StringUtils.isBlank(nombre) ? StringUtils.EMPTY : StringUtils.capitalize(StringUtils.trim(nombre));
    }

    // Metodo para devolver el email del usuario normalizado en caso no estar en blanco
    public String normalizarEmail(String email) {
        return StringUtils.isBlank(email) ? StringUtils.EMPTY : StringUtils.lowerCase(StringUtils.strip(email));
    }

    // Metodo para generar un codigo de Usuario: {nombre_usuario}_{id_usuario} en miniscula
    public String generarCodigo(String nombre, Long id) {
        if (StringUtils.isBlank(nombre) || id == null) {
            return StringUtils.EMPTY;
        }
        String base = StringUtils.trim(nombre) + "_" + id;
        return StringUtils.upperCase(base);
    }
 
 
}