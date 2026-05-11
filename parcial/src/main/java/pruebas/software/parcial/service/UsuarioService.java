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
        if (StringUtils.isBlank(nombre) || id == null) return StringUtils.EMPTY;

        String base = StringUtils.trim(nombre) + "_" + id;
        return StringUtils.upperCase(base);
    }
 
    // Metodo que simplifica y devuelve la biografia del usuario a 50 caracteres
    public String resumirBiografia(String biografia) {
        if (StringUtils.isBlank(biografia)) return StringUtils.EMPTY;

        return StringUtils.abbreviate(StringUtils.trim(biografia), 50);
    }

    // Metodo que verifica que el dominicio del email del usuario este presente
    public boolean emailEsDeDominio(String email, String dominio) {
        return StringUtils.containsIgnoreCase(email, dominio);
    }

    // Metodo que verifica si el nombre de usuario comienza con una letra y tiene entre 4 y 20 caracteres
    public boolean usernameValido(String username) {
        if (StringUtils.isBlank(username)) return false;

        return StringUtils.startsWithAny(username, "a","b","c","d","e","f","g","h",
                "i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
                "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R",
                "S","T","U","V","W","X","Y","Z")
                && username.length() >= 4 && username.length() <= 20;
    }

    // Metodo que genera un Token para un usuario: {NOMBRE_USUARIO}_VRF
    public String generarTokenVerificacion(String username) {
        if (StringUtils.isBlank(username)) return StringUtils.EMPTY;
        return StringUtils.reverse(StringUtils.upperCase(username)) + "_VRF";
    }
}