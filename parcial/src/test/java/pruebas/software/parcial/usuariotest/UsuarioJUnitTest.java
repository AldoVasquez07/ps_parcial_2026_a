package pruebas.software.parcial.usuariotest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import pruebas.software.parcial.model.Usuario;
import pruebas.software.parcial.repository.UsuarioRepository;
import pruebas.software.parcial.service.UsuarioService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Usuario con JUnit")
class UsuarioJUnitTest {

    static class RepositorioEnMemoria implements UsuarioRepository {
        private final java.util.Map<Long, Usuario> db = new java.util.HashMap<>();
        private Long nextId = 1L;

        @Override
        public Optional<Usuario> findById(Long id) {
            return Optional.ofNullable(db.get(id));
        }

        @Override
        public Optional<Usuario> findByUsername(String username) {
            return db.values().stream()
                    .filter(u -> username.equals(u.getUsername()))
                    .findFirst();
        }

        @Override
        public Optional<Usuario> findByEmail(String email) {
            return db.values().stream()
                    .filter(u -> email.equals(u.getEmail()))
                    .findFirst();
        }

        @Override
        public Usuario save(Usuario usuario) {
            if (usuario.getId() == null) usuario.setId(nextId++);
            db.put(usuario.getId(), usuario);
            return usuario;
        }

        @Override
        public boolean existsByUsername(String username) {
            return db.values().stream().anyMatch(u -> username.equals(u.getUsername()));
        }

        @Override
        public boolean existsByEmail(String email) {
            return db.values().stream().anyMatch(u -> email.equals(u.getEmail()));
        }

        public void limpiar() { db.clear(); nextId = 1L; }
    }

    private RepositorioEnMemoria repositorio;
    private UsuarioService        service;

    @BeforeEach
    void setUp() {
        repositorio = new RepositorioEnMemoria();
        service     = new UsuarioService(repositorio);
    }

    @Nested
    @DisplayName("Pruebas con: validarCamposObligatorios")
    class Suite1_CamposObligatorios {

        @Test
        @DisplayName("Nombre y email presentes → true")
        void ambosPresentes() {
            assertTrue(service.validarCamposObligatorios("Juan", "juan@mail.com"));
        }

        @ParameterizedTest(name = "nombre=\"{0}\" → false")
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t"})
        @DisplayName("Nombre en blanco → false")
        void nombreEnBlanco(String nombre) {
            assertFalse(service.validarCamposObligatorios(nombre, "email@x.com"));
        }

        @ParameterizedTest(name = "email=\"{0}\" → false")
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("Email en blanco → false")
        void emailEnBlanco(String email) {
            assertFalse(service.validarCamposObligatorios("Juan", email));
        }

        @Test
        @DisplayName("Ambos en blanco → false")
        void ambosEnBlanco() {
            assertFalse(service.validarCamposObligatorios("", ""));
        }
    }

    @Nested
    @DisplayName("Pruebas con: normalizarNombre (capitalize + trim)")
    class Suite2_NormalizarNombre {

        @Test
        @DisplayName("Nombre con espacios y minúsculas → capitalizado y sin espacios")
        void normalizar_ok() {
            assertEquals("Juan", service.normalizarNombre("  juan  "));
        }

        @Test
        @DisplayName("Nombre null → cadena vacía")
        void normalizar_null() {
            assertEquals("", service.normalizarNombre(null));
        }

        @Test
        @DisplayName("Nombre en blanco → cadena vacía")
        void normalizar_blank() {
            assertEquals("", service.normalizarNombre("   "));
        }

        @Test
        @DisplayName("Nombre ya capitalizado → sin cambio")
        void normalizar_yaCap() {
            assertEquals("Pedro", service.normalizarNombre("Pedro"));
        }

        @ParameterizedTest(name = "\"{0}\" → \"{1}\"")
        @CsvSource({"carlos,Carlos", "  ana  ,Ana", "MARIA,MARIA"})
        @DisplayName("normalizarNombre: tabla de casos")
        void normalizar_tabla(String input, String expected) {
            assertEquals(expected, service.normalizarNombre(input));
        }
    }

    @Nested
    @DisplayName("Pruebas con: normalizarEmail (lowerCase + strip)")
    class Suite3_NormalizarEmail {

        @Test
        @DisplayName("Email con mayúsculas y espacios → minúsculas y sin espacios")
        void normalizar_ok() {
            assertEquals("juan@gmail.com", service.normalizarEmail("  JUAN@GMAIL.COM  "));
        }

        @Test
        @DisplayName("Email null → cadena vacía")
        void normalizar_null() {
            assertEquals("", service.normalizarEmail(null));
        }

        @Test
        @DisplayName("Email ya normalizado → sin cambio")
        void normalizar_yaNorm() {
            assertEquals("user@domain.com", service.normalizarEmail("user@domain.com"));
        }
    }

    @Nested
    @DisplayName("Pruebas con: generarCodigo (upperCase)")
    class Suite4_GenerarCodigo {

        @Test
        @DisplayName("Nombre e ID válidos → código en mayúsculas")
        void codigo_ok() {
            assertEquals("CARLOS_42", service.generarCodigo("carlos", 42L));
        }

        @Test
        @DisplayName("Nombre con espacios → se recorta antes de generar código")
        void codigo_espacios() {
            assertEquals("ANA_1", service.generarCodigo("  ana  ", 1L));
        }

        @Test
        @DisplayName("Nombre null → cadena vacía")
        void codigo_nombreNull() {
            assertEquals("", service.generarCodigo(null, 1L));
        }

        @Test
        @DisplayName("ID null → cadena vacía")
        void codigo_idNull() {
            assertEquals("", service.generarCodigo("carlos", null));
        }
    }

    @Nested
    @DisplayName("Pruebas con: resumirBiografia (abbreviate)")
    class Suite5_ResumirBiografia {

        @Test
        @DisplayName("Bio larga → abreviada a ≤ 50 chars con '...'")
        void bio_larga() {
            String bio = "Esta es una biografía muy extensa que supera con creces los cincuenta caracteres permitidos en el sistema";
            String resultado = service.resumirBiografia(bio);
            assertTrue(resultado.length() <= 50);
            assertTrue(resultado.endsWith("..."));
        }

        @Test
        @DisplayName("Bio corta → devuelta sin cambio")
        void bio_corta() {
            assertEquals("Hola mundo", service.resumirBiografia("Hola mundo"));
        }

        @Test
        @DisplayName("Bio null → cadena vacía")
        void bio_null() {
            assertEquals("", service.resumirBiografia(null));
        }

        @Test
        @DisplayName("Bio exactamente 50 chars → sin abreviar")
        void bio_exacta50() {
            String exacta = "a".repeat(50);
            assertEquals(exacta, service.resumirBiografia(exacta));
        }
    }

    @Nested
    @DisplayName("Pruebas con: emailEsDeDominio (containsIgnoreCase)")
    class Suite6_EmailDominio {

        @Test
        @DisplayName("Dominio en minúsculas → true")
        void dominio_ok() {
            assertTrue(service.emailEsDeDominio("user@empresa.com", "empresa"));
        }

        @Test
        @DisplayName("Dominio en mayúsculas → true (case-insensitive)")
        void dominio_mayusculas() {
            assertTrue(service.emailEsDeDominio("USER@EMPRESA.COM", "empresa"));
        }

        @Test
        @DisplayName("Dominio diferente → false")
        void dominio_diferente() {
            assertFalse(service.emailEsDeDominio("user@otro.com", "empresa"));
        }
    }

    @Nested
    @DisplayName("Pruebas con: usernameValido (startsWithAny)")
    class Suite7_UsernameValido {

        @Test
        @DisplayName("Username válido → true")
        void username_ok() {
            assertTrue(service.usernameValido("juan01"));
        }

        @Test
        @DisplayName("Inicia con número → false")
        void username_iniciaNumero() {
            assertFalse(service.usernameValido("1juan"));
        }

        @Test
        @DisplayName("Menos de 4 chars → false")
        void username_muyCorto() {
            assertFalse(service.usernameValido("ab"));
        }

        @Test
        @DisplayName("Más de 20 chars → false")
        void username_muyLargo() {
            assertFalse(service.usernameValido("a".repeat(21)));
        }

        @Test
        @DisplayName("Exactamente 4 chars → true")
        void username_exacto4() {
            assertTrue(service.usernameValido("abcd"));
        }

        @Test
        @DisplayName("Username null → false")
        void username_null() {
            assertFalse(service.usernameValido(null));
        }
    }

    @Nested
    @DisplayName("Pruebas con: generarTokenVerificacion (reverse + upperCase)")
    class Suite8_Token {

        @Test
        @DisplayName("\"juan\" → \"NAUJ_VRF\"")
        void token_ok() {
            assertEquals("NAUJ_VRF", service.generarTokenVerificacion("juan"));
        }

        @Test
        @DisplayName("Username nulo → cadena vacía")
        void token_null() {
            assertEquals("", service.generarTokenVerificacion(null));
        }

        @Test
        @DisplayName("Username en blanco → cadena vacía")
        void token_blank() {
            assertEquals("", service.generarTokenVerificacion("   "));
        }

        @Test
        @DisplayName("Token siempre termina con '_VRF'")
        void token_sufijo() {
            assertTrue(service.generarTokenVerificacion("carlos").endsWith("_VRF"));
        }
    }

    @Nested
    @DisplayName("Pruebas con: formatearId / lineaReporte (leftPad / rightPad)")
    class Suite9_Padding {

        @Test
        @DisplayName("formatearId(42) → \"00000042\"")
        void formatearId_ok() {
            assertEquals("00000042", service.formatearId(42L));
        }

        @Test
        @DisplayName("formatearId(null) → cadena vacía")
        void formatearId_null() {
            assertEquals("", service.formatearId(null));
        }

        @Test
        @DisplayName("formatearId siempre produce exactamente 8 caracteres")
        void formatearId_longitud() {
            assertEquals(8, service.formatearId(1L).length());
            assertEquals(8, service.formatearId(9999999L).length());
        }

        @Test
        @DisplayName("lineaReporte: resultado de exactamente 20 chars")
        void lineaReporte_longitud() {
            assertEquals(20, service.lineaReporte("juan").length());
        }

        @Test
        @DisplayName("lineaReporte: nombre capitalizado al inicio")
        void lineaReporte_capitalizado() {
            assertTrue(service.lineaReporte("ana").startsWith("Ana"));
        }

        @Test
        @DisplayName("lineaReporte(null) → cadena vacía")
        void lineaReporte_null() {
            assertEquals("", service.lineaReporte(null));
        }
    }

    @Nested
    @DisplayName("Pruebas con: contarPalabraEnBiografia (countMatches)")
    class Suite10_ContarPalabra {

        @Test
        @DisplayName("Palabra aparece 3 veces → 3")
        void contar_tres() {
            int n = service.contarPalabraEnBiografia(
                    "Java es genial, Java es potente, Java es popular", "Java");
            assertEquals(3, n);
        }

        @Test
        @DisplayName("Palabra ausente → 0")
        void contar_cero() {
            assertEquals(0, service.contarPalabraEnBiografia("Soy dev", "Python"));
        }

        @Test
        @DisplayName("Bio null → 0")
        void contar_bioNull() {
            assertEquals(0, service.contarPalabraEnBiografia(null, "Java"));
        }

        @Test
        @DisplayName("Palabra null → 0")
        void contar_palabraNull() {
            assertEquals(0, service.contarPalabraEnBiografia("texto", null));
        }
    }

    @Nested
    @DisplayName("Pruebas con: censurarBiografia (replace)")
    class Suite11_Censurar {

        @Test
        @DisplayName("Reemplaza todas las ocurrencias por '***'")
        void censurar_ok() {
            assertEquals("Me encanta el *** y más ***",
                    service.censurarBiografia("Me encanta el spam y más spam", "spam"));
        }

        @Test
        @DisplayName("Sin palabra prohibida → texto original")
        void censurar_sinOcurrencia() {
            assertEquals("Texto limpio", service.censurarBiografia("Texto limpio", "spam"));
        }

        @Test
        @DisplayName("Bio null → cadena vacía")
        void censurar_null() {
            assertEquals("", service.censurarBiografia(null, "spam"));
        }
    }

    @Nested
    @DisplayName("Pruebas con: sonMismoUsuario (equals null-safe)")
    class Suite12_MismoUsuario {

        @Test
        @DisplayName("Mismos nombres → true")
        void mismo_iguales() {
            assertTrue(service.sonMismoUsuario("admin", "admin"));
        }

        @Test
        @DisplayName("Diferente caja → true (normalización a minúsculas)")
        void mismo_caja() {
            assertTrue(service.sonMismoUsuario("Admin", "admin"));
        }

        @Test
        @DisplayName("Nombres distintos → false")
        void mismo_distintos() {
            assertFalse(service.sonMismoUsuario("admin", "user01"));
        }

        @Test
        @DisplayName("Ambos null → true (null-safe)")
        void mismo_ambosNull() {
            assertTrue(service.sonMismoUsuario(null, null));
        }

        @Test
        @DisplayName("Uno null, otro no → false")
        void mismo_unNull() {
            assertFalse(service.sonMismoUsuario(null, "admin"));
        }
    }

    @Nested
    @DisplayName("Pruebas con: nombreSoloLetras / telefonoSoloNumeros")
    class Suite13_Validaciones {

        @Test
        @DisplayName("Nombre solo letras → true")
        void nombre_ok() {
            assertTrue(service.nombreSoloLetras("Ana"));
        }

        @Test
        @DisplayName("Nombre con número → false")
        void nombre_conNumero() {
            assertFalse(service.nombreSoloLetras("Ana1"));
        }

        @Test
        @DisplayName("Nombre null → false")
        void nombre_null() {
            assertFalse(service.nombreSoloLetras(null));
        }

        @Test
        @DisplayName("Teléfono solo dígitos → true")
        void telefono_ok() {
            assertTrue(service.telefonoSoloNumeros("987654321"));
        }

        @Test
        @DisplayName("Teléfono con letra → false")
        void telefono_letra() {
            assertFalse(service.telefonoSoloNumeros("98765A321"));
        }

        @Test
        @DisplayName("Teléfono null → false")
        void telefono_null() {
            assertFalse(service.telefonoSoloNumeros(null));
        }
    }

    @Nested
    @DisplayName("Pruebas con: nombreCompleto (join + capitalize)")
    class Suite14_NombreCompleto {

        @Test
        @DisplayName("Une nombre y apellido con espacio")
        void completo_ok() {
            assertEquals("Juan Pérez", service.nombreCompleto("juan", "pérez"));
        }

        @Test
        @DisplayName("Con espacios extra → recortado y capitalizado")
        void completo_espacios() {
            assertEquals("Ana López", service.nombreCompleto("  ana  ", "  lópez  "));
        }
    }

    @Nested
    @DisplayName("Pruebas con: registrarUsuario (repo en memoria)")
    class Suite15_RegistrarUsuario {

        private Usuario usuarioBase() {
            return Usuario.builder()
                    .nombre("juan")
                    .apellido("perez")
                    .email("  JUAN@GMAIL.COM  ")
                    .username("juandev")
                    .biografia("Desarrollador Java con más de diez años de experiencia y trayectoria en proyectos")
                    .build();
        }

        @Test
        @DisplayName("Registro exitoso: persiste en repositorio con campos normalizados")
        void registrar_exitoso() {
            Usuario resultado = service.registrarUsuario(usuarioBase());

            assertNotNull(resultado.getId());
            assertEquals("Juan",           resultado.getNombre());
            assertEquals("Perez",          resultado.getApellido());
            assertEquals("juan@gmail.com", resultado.getEmail());
            assertTrue(resultado.getBiografia().endsWith("..."));
            assertTrue(resultado.getBiografia().length() <= 50);

            // Verificar persistencia real
            Optional<Usuario> enRepo = repositorio.findById(resultado.getId());
            assertTrue(enRepo.isPresent());
        }

        @Test
        @DisplayName("Nombre en blanco → IllegalArgumentException")
        void registrar_nombreBlanco() {
            Usuario u = usuarioBase();
            u.setNombre("  ");
            assertThrows(IllegalArgumentException.class,
                    () -> service.registrarUsuario(u));
        }

        @Test
        @DisplayName("Email en blanco → IllegalArgumentException")
        void registrar_emailBlanco() {
            Usuario u = usuarioBase();
            u.setEmail("");
            assertThrows(IllegalArgumentException.class,
                    () -> service.registrarUsuario(u));
        }

        @Test
        @DisplayName("Nombre con dígitos → IllegalArgumentException (isAlpha)")
        void registrar_nombreConDigitos() {
            Usuario u = usuarioBase();
            u.setNombre("Juan123");
            assertThrows(IllegalArgumentException.class,
                    () -> service.registrarUsuario(u));
        }

        @Test
        @DisplayName("Username corto → IllegalArgumentException")
        void registrar_usernameCorto() {
            Usuario u = usuarioBase();
            u.setUsername("ab");
            assertThrows(IllegalArgumentException.class,
                    () -> service.registrarUsuario(u));
        }

        @Test
        @DisplayName("Registro de dos usuarios distintos → IDs diferentes")
        void registrar_dosUsuarios() {
            Usuario u1 = service.registrarUsuario(usuarioBase());
            Usuario u2 = service.registrarUsuario(
                    Usuario.builder()
                            .nombre("Ana")
                            .apellido("Gomez")
                            .email("ana@mail.com")
                            .username("anadev")
                            .build());

            assertNotEquals(u1.getId(), u2.getId());
        }
    }

    @Nested
    @DisplayName("Pruebas con: buscarPorUsername (lowerCase + trim)")
    class Suite16_BuscarUsername {

        @Test
        @DisplayName("Buscar username existente → Optional con usuario")
        void buscar_encontrado() {
            repositorio.save(Usuario.builder().id(1L).username("adminuser").build());
            Optional<Usuario> resultado = service.buscarPorUsername("adminuser");
            assertTrue(resultado.isPresent());
        }

        @Test
        @DisplayName("Username con mayúsculas y espacios → normalizado antes de buscar")
        void buscar_normalizado() {
            repositorio.save(Usuario.builder().id(1L).username("adminuser").build());
            Optional<Usuario> resultado = service.buscarPorUsername("  ADMINUSER  ");
            assertTrue(resultado.isPresent());
        }

        @Test
        @DisplayName("Username inexistente → Optional vacío")
        void buscar_noEncontrado() {
            assertTrue(service.buscarPorUsername("fantasma").isEmpty());
        }

        @Test
        @DisplayName("Username null → Optional vacío sin consultar repo")
        void buscar_null() {
            assertTrue(service.buscarPorUsername(null).isEmpty());
        }

        @Test
        @DisplayName("Username en blanco → Optional vacío")
        void buscar_blank() {
            assertTrue(service.buscarPorUsername("   ").isEmpty());
        }
    }

    @Nested
    @DisplayName("Pruebas con: generarPerfilPublico (join + upperCase + leftPad)")
    class Suite17_PerfilPublico {

        @Test
        @DisplayName("Perfil contiene ID formateado con ceros")
        void perfil_idFormateado() {
            Usuario u = Usuario.builder().id(7L).nombre("ana")
                    .apellido("gomez").biografia("Ingeniera").build();
            assertTrue(service.generarPerfilPublico(u).contains("00000007"));
        }

        @Test
        @DisplayName("Perfil contiene nombre en mayúsculas")
        void perfil_nombreMayusculas() {
            Usuario u = Usuario.builder().id(1L).nombre("luis")
                    .apellido("rios").biografia("Dev").build();
            assertTrue(service.generarPerfilPublico(u).contains("LUIS RIOS"));
        }

        @Test
        @DisplayName("Perfil sin bio → muestra 'Sin biografía'")
        void perfil_sinBio() {
            Usuario u = Usuario.builder().id(3L).nombre("luis")
                    .apellido("rios").biografia(null).build();
            assertTrue(service.generarPerfilPublico(u).contains("Sin biografía"));
        }

        @Test
        @DisplayName("Bio larga → abreviada en el perfil (≤ 50 chars)")
        void perfil_bioLarga() {
            Usuario u = Usuario.builder().id(99L).nombre("maria").apellido("castro")
                    .biografia("Esta es una biografía extremadamente larga que con certeza supera el límite permitido")
                    .build();
            String perfil = service.generarPerfilPublico(u);
            String bioEnPerfil = perfil.split("BIO: ")[1];
            assertTrue(bioEnPerfil.length() <= 50);
        }

        @Test
        @DisplayName("Formato del perfil: contiene separador ' | '")
        void perfil_separador() {
            Usuario u = Usuario.builder().id(1L).nombre("pedro")
                    .apellido("pardo").biografia("Dev").build();
            assertTrue(service.generarPerfilPublico(u).contains(" | "));
        }
    }
}