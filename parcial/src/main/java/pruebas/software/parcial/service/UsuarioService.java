package pruebas.software.parcial.service;
 
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pruebas.software.parcial.model.Usuario;
import pruebas.software.parcial.repository.UsuarioRepository;
 
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

    // Metodo que formatea el id del usuario: 00000000{id}
    public String formatearId(Long id) {
        return (id == null) ? StringUtils.EMPTY : StringUtils.leftPad(id.toString(), 8, '0');
    }

    // Metodo que genera una linea de reporte con nombre de usuario alineado a la derecha
    public String lineaReporte(String nombre) {
        if (StringUtils.isBlank(nombre)) return StringUtils.EMPTY;
        return StringUtils.rightPad(StringUtils.capitalize(StringUtils.trim(nombre)), 20, ' ');
    }

    // Metodo que cuenta la cantidad de una palabra en la biografia del usuario
    public int contarPalabraEnBiografia(String biografia, String palabra) {
        if (StringUtils.isBlank(biografia) || StringUtils.isBlank(palabra)) return 0;
        return StringUtils.countMatches(biografia, palabra);
    }

    // Metodo que censura palabras especificas en la biografia del usuario a: "***"
    public String censurarBiografia(String biografia, String palabraProhibida) {
        if (StringUtils.isBlank(biografia)) return StringUtils.EMPTY;
        return StringUtils.replace(biografia, palabraProhibida, "***");
    }

    // Metodo que verifica si dos usuario usuarios son los mismos
    public boolean sonMismoUsuario(String u1, String u2) {
        return StringUtils.equals(StringUtils.lowerCase(u1), StringUtils.lowerCase(u2));
    }

    public boolean nombreSoloLetras(String nombre) {
        if (StringUtils.isBlank(nombre)) return false;
        return StringUtils.isAlpha(StringUtils.trim(nombre));
    }
 
    
    // Metodo que valida que el teléfono solo contenga dígitos
    public boolean telefonoSoloNumeros(String telefono) {
        if (StringUtils.isBlank(telefono)) return false;
        return StringUtils.isNumeric(StringUtils.trim(telefono));
    }
 
    // Metodo que genera el nombre completo uniendo nombre y apellido
    public String nombreCompleto(String nombre, String apellido) {
        return StringUtils.join(new String[]{
                StringUtils.capitalize(StringUtils.trim(nombre)),
                StringUtils.capitalize(StringUtils.trim(apellido))
        }, " ");
    }
 
    // Registra un usuario después de normalizar y validar sus campos
    // Orquesta multiples llamadas a StringUtils internamente
    public Usuario registrarUsuario(Usuario usuario) {
        if (!validarCamposObligatorios(usuario.getNombre(), usuario.getEmail())) {
            throw new IllegalArgumentException("Nombre y email son obligatorios");
        }
        if (!nombreSoloLetras(usuario.getNombre())) {
            throw new IllegalArgumentException("El nombre solo debe contener letras");
        }
        if (!usernameValido(usuario.getUsername())) {
            throw new IllegalArgumentException("Username inválido");
        }
 
        usuario.setNombre(normalizarNombre(usuario.getNombre()));
        usuario.setApellido(normalizarNombre(usuario.getApellido()));
        usuario.setEmail(normalizarEmail(usuario.getEmail()));
        usuario.setBiografia(resumirBiografia(usuario.getBiografia()));
 
        return repository.save(usuario);
    }
 
    // Busca un usuario por username
    public Optional<Usuario> buscarPorUsername(String username) {
        if (StringUtils.isBlank(username)) return Optional.empty();
        return repository.findByUsername(StringUtils.lowerCase(StringUtils.trim(username)));
    }
 
    // Genera el perfil público resumido del usuario.
    public String generarPerfilPublico(Usuario usuario) {
        String nombre    = normalizarNombre(usuario.getNombre());
        String apellido  = normalizarNombre(usuario.getApellido());
        String bio       = resumirBiografia(usuario.getBiografia());
        String idFormato = formatearId(usuario.getId());
 
        return StringUtils.join(new String[]{
                "ID: "     + idFormato,
                "NOMBRE: " + StringUtils.upperCase(nombreCompleto(nombre, apellido)),
                "BIO: "    + (StringUtils.isBlank(bio) ? "Sin biografía" : bio)
        }, " | ");
    }
}