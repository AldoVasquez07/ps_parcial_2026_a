package pruebas.software.parcial.mockito;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import pruebas.software.parcial.model.Usuario;
import pruebas.software.parcial.repository.UsuarioRepository;
import pruebas.software.parcial.service.UsuarioService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService con Mockito")
class UsuarioMockitoTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioService service;

    @Captor
    private ArgumentCaptor<Usuario> usuarioCaptor;


    @Nested
    @DisplayName("Suite – validarCamposObligatorios (isBlank/isNotBlank)")
    class Suite1_ValidarCampos {

        @Test
        @DisplayName("Nombre y email presentes → true; repositorio NO es consultado")
        void camposValidos_noInteraccionRepo() {
            boolean resultado = service.validarCamposObligatorios("Juan", "juan@mail.com");
            assertTrue(resultado);
            verifyNoInteractions(repository);
        }

        @ParameterizedTest(name = "nombre=\"{0}\" → false")
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t"})
        @DisplayName("Nombre en blanco → false")
        void nombreEnBlanco_false(String nombre) {
            assertFalse(service.validarCamposObligatorios(nombre, "email@x.com"));
            verifyNoInteractions(repository);
        }

        @ParameterizedTest(name = "email=\"{0}\" → false")
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("Email en blanco → false")
        void emailEnBlanco_false(String email) {
            assertFalse(service.validarCamposObligatorios("Juan", email));
            verifyNoInteractions(repository);
        }
    }

    @Nested
    @DisplayName("Suite – normalizarNombre (capitalize/trim)")
    class Suite2_NormalizarNombre {

        @Test
        @DisplayName("Nombre con espacios y minúsculas → capitalizado sin espacios")
        void normalizarNombre_capitalizado() {
            String resultado = service.normalizarNombre("  juan  ");
            assertEquals("Juan", resultado);
            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("Nombre null → cadena vacía")
        void normalizarNombre_null() {
            assertEquals("", service.normalizarNombre(null));
        }

        @Test
        @DisplayName("Nombre en blanco → cadena vacía")
        void normalizarNombre_blank() {
            assertEquals("", service.normalizarNombre("   "));
        }

        @Test
        @DisplayName("Nombre ya capitalizado → sin cambio")
        void normalizarNombre_yaCapitalizado() {
            assertEquals("Pedro", service.normalizarNombre("Pedro"));
        }
    }

    @Nested
    @DisplayName("Suite – normalizarEmail (lowerCase/strip)")
    class Suite3_NormalizarEmail {

        @Test
        @DisplayName("Email con mayúsculas y espacios → normalizado")
        void normalizarEmail_ok() {
            String resultado = service.normalizarEmail("  JUAN@GMAIL.COM  ");
            assertEquals("juan@gmail.com", resultado);
            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("Email null → cadena vacía")
        void normalizarEmail_null() {
            assertEquals("", service.normalizarEmail(null));
        }

        @Test
        @DisplayName("Email ya normalizado → sin cambio")
        void normalizarEmail_yaNormalizado() {
            assertEquals("user@domain.com", service.normalizarEmail("user@domain.com"));
        }
    }

    @Nested
    @DisplayName("Suite – generarCodigo (upperCase)")
    class Suite4_GenerarCodigo {

        @Test
        @DisplayName("Nombre e ID válidos → código en mayúsculas")
        void generarCodigo_ok() {
            String resultado = service.generarCodigo("carlos", 42L);
            assertEquals("CARLOS_42", resultado);
            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("Nombre nulo → cadena vacía")
        void generarCodigo_nombreNull() {
            assertEquals("", service.generarCodigo(null, 1L));
        }

        @Test
        @DisplayName("ID nulo → cadena vacía")
        void generarCodigo_idNull() {
            assertEquals("", service.generarCodigo("carlos", null));
        }
    }

    @Nested
    @DisplayName("Suite – resumirBiografia (abbreviate)")
    class Suite5_ResumirBiografia {

        @Test
        @DisplayName("Biografía larga → abreviada a ≤ 50 chars con '...'")
        void resumirBiografia_larga() {
            String bio = "Esta es una biografía muy extensa que supera ampliamente el límite permitido de cincuenta caracteres";
            String resultado = service.resumirBiografia(bio);
            assertTrue(resultado.length() <= 50);
            assertTrue(resultado.endsWith("..."));
            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("Biografía corta → sin cambio")
        void resumirBiografia_corta() {
            assertEquals("Hola mundo", service.resumirBiografia("Hola mundo"));
        }

        @Test
        @DisplayName("Biografía null → cadena vacía")
        void resumirBiografia_null() {
            assertEquals("", service.resumirBiografia(null));
        }

        @Test
        @DisplayName("Biografía con espacios laterales → recortada antes de abreviar")
        void resumirBiografia_conEspacios() {
            String resultado = service.resumirBiografia("  Hola  ");
            assertEquals("Hola", resultado);
        }
    }

    @Nested
    @DisplayName("Suite – emailEsDeDominio (containsIgnoreCase)")
    class Suite6_EmailDominio {

        @Test
        @DisplayName("Email con dominio en minúsculas → true")
        void emailDominio_ok() {
            assertTrue(service.emailEsDeDominio("user@empresa.com", "empresa"));
            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("Email con dominio en mayúsculas → true (case-insensitive)")
        void emailDominio_caseInsensitive() {
            assertTrue(service.emailEsDeDominio("USER@EMPRESA.COM", "empresa"));
        }

        @Test
        @DisplayName("Dominio diferente → false")
        void emailDominio_diferente() {
            assertFalse(service.emailEsDeDominio("user@otro.com", "empresa"));
        }
    }

    @Nested
    @DisplayName("Suite – usernameValido (startsWith)")
    class Suite7_UsernameValido {

        @Test
        @DisplayName("Username válido → true")
        void username_valido() {
            assertTrue(service.usernameValido("juan01"));
            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("Username que inicia con número → false")
        void username_iniciaConNumero() {
            assertFalse(service.usernameValido("1juan"));
        }

        @Test
        @DisplayName("Username menor a 4 chars → false")
        void username_muyCorto() {
            assertFalse(service.usernameValido("ab"));
        }

        @Test
        @DisplayName("Username mayor a 20 chars → false")
        void username_muyLargo() {
            assertFalse(service.usernameValido("a".repeat(21)));
        }

        @Test
        @DisplayName("Username nulo → false")
        void username_null() {
            assertFalse(service.usernameValido(null));
        }
    }

    @Nested
    @DisplayName("Suite – generarTokenVerificacion (reverse/upperCase)")
    class Suite8_Token {

        @Test
        @DisplayName("Token generado correctamente: username invertido + sufijo")
        void token_generado() {
            String token = service.generarTokenVerificacion("juan");
            assertEquals("NAUJ_VRF", token);
            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("Username null → cadena vacía")
        void token_null() {
            assertEquals("", service.generarTokenVerificacion(null));
        }

        @Test
        @DisplayName("Username en blanco → cadena vacía")
        void token_blank() {
            assertEquals("", service.generarTokenVerificacion("   "));
        }
    }

    @Nested
    @DisplayName("Suite – formatearId / lineaReporte (leftPad/rightPad)")
    class Suite9_Padding {

        @Test
        @DisplayName("ID 42 → '00000042' (8 dígitos con ceros)")
        void formatearId_ok() {
            assertEquals("00000042", service.formatearId(42L));
            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("ID null → cadena vacía")
        void formatearId_null() {
            assertEquals("", service.formatearId(null));
        }

        @Test
        @DisplayName("Línea de reporte: nombre alineado a 20 chars")
        void lineaReporte_ok() {
            String linea = service.lineaReporte("juan");
            assertEquals(20, linea.length());
            assertTrue(linea.startsWith("Juan"));
        }

        @Test
        @DisplayName("Línea de reporte: nombre null → cadena vacía")
        void lineaReporte_null() {
            assertEquals("", service.lineaReporte(null));
        }
    }

    @Nested
    @DisplayName("Suite – contarPalabraEnBiografia (countMatches)")
    class Suite10_ContarPalabra {

        @Test
        @DisplayName("Palabra aparece 3 veces → 3")
        void contar_tresOcurrencias() {
            int n = service.contarPalabraEnBiografia("Java es genial, Java es potente, Java es ubicuo", "Java");
            assertEquals(3, n);
            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("Palabra ausente → 0")
        void contar_sinOcurrencia() {
            assertEquals(0, service.contarPalabraEnBiografia("Soy desarrollador", "Python"));
        }

        @Test
        @DisplayName("Biografía null → 0")
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
    @DisplayName("Suite – censurarBiografia (replace)")
    class Suite11_Censurar {

        @Test
        @DisplayName("Censura la palabra prohibida con ***")
        void censurar_ok() {
            String resultado = service.censurarBiografia("Me encanta el spam y más spam", "spam");
            assertEquals("Me encanta el *** y más ***", resultado);
            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("Sin palabra prohibida → texto sin cambio")
        void censurar_sinOcurrencia() {
            assertEquals("Texto limpio", service.censurarBiografia("Texto limpio", "spam"));
        }

        @Test
        @DisplayName("Biografía null → cadena vacía")
        void censurar_null() {
            assertEquals("", service.censurarBiografia(null, "spam"));
        }
    }

    @Nested
    @DisplayName("Suite – sonMismoUsuario (equals null-safe)")
    class Suite12_MismoUsuario {

        @Test
        @DisplayName("Mismos usernames → true")
        void mismo_iguales() {
            assertTrue(service.sonMismoUsuario("admin", "admin"));
            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("Diferente caja → true (se normaliza a minúsculas)")
        void mismo_diferenteCaja() {
            assertTrue(service.sonMismoUsuario("Admin", "admin"));
        }

        @Test
        @DisplayName("Usernames distintos → false")
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
    @DisplayName("Suite – nombreSoloLetras / telefonoSoloNumeros (isAlpha/isNumeric)")
    class Suite13_Validaciones {

        @Test
        @DisplayName("Nombre solo letras → true")
        void nombre_soloLetras() {
            assertTrue(service.nombreSoloLetras("Ana"));
            verifyNoInteractions(repository);
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
        void telefono_soloDigitos() {
            assertTrue(service.telefonoSoloNumeros("987654321"));
        }

        @Test
        @DisplayName("Teléfono con letra → false")
        void telefono_conLetra() {
            assertFalse(service.telefonoSoloNumeros("98765A321"));
        }

        @Test
        @DisplayName("Teléfono null → false")
        void telefono_null() {
            assertFalse(service.telefonoSoloNumeros(null));
        }
    }

    @Nested
    @DisplayName("Suite – nombreCompleto (join/capitalize)")
    class Suite14_NombreCompleto {

        @Test
        @DisplayName("Nombre y apellido → nombre completo capitalizado")
        void nombreCompleto_ok() {
            assertEquals("Juan Pérez", service.nombreCompleto("juan", "pérez"));
            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("Nombre con espacios extra → recortado y capitalizado")
        void nombreCompleto_espacios() {
            assertEquals("Ana López", service.nombreCompleto("  ana  ", "  lópez  "));
        }
    }

    @Nested
    @DisplayName("Suite – registrarUsuario (flujo completo con Mock)")
    class Suite15_RegistrarUsuario {

        private Usuario usuarioValido() {
            return Usuario.builder()
                    .nombre("juan")
                    .apellido("perez")
                    .email("  JUAN@GMAIL.COM  ")
                    .username("juandev")
                    .biografia("Desarrollador Java con más de diez años de experiencia profesional en el sector tecnológico")
                    .build();
        }

        @Test
        @DisplayName("Registro exitoso: normaliza campos y llama save una vez")
        void registrar_exitoso() {
            Usuario input = usuarioValido();
            Usuario guardado = usuarioValido();
            guardado.setId(1L);
            when(repository.save(any(Usuario.class))).thenReturn(guardado);

            Usuario resultado = service.registrarUsuario(input);

            // Verificar interacción
            verify(repository, times(1)).save(usuarioCaptor.capture());
            verifyNoMoreInteractions(repository);

            // Versificar normalización (StringUtils.capitalize, lowerCase, abbreviate)
            Usuario capturado = usuarioCaptor.getValue();
            assertEquals("Juan", capturado.getNombre());
            assertEquals("Perez", capturado.getApellido());
            assertEquals("juan@gmail.com", capturado.getEmail());
            assertTrue(capturado.getBiografia().endsWith("..."));
            assertTrue(capturado.getBiografia().length() <= 50);

            assertNotNull(resultado.getId());
        }

        @Test
        @DisplayName("Nombre en blanco → excepción, repositorio NO invocado")
        void registrar_nombreBlanco() {
            Usuario u = usuarioValido();
            u.setNombre("  ");

            assertThrows(IllegalArgumentException.class,
                    () -> service.registrarUsuario(u));
            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("Email en blanco → excepción, repositorio NO invocado")
        void registrar_emailBlanco() {
            Usuario u = usuarioValido();
            u.setEmail("");

            assertThrows(IllegalArgumentException.class,
                    () -> service.registrarUsuario(u));
            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("Nombre con números → excepción (isAlpha falla)")
        void registrar_nombreConNumeros() {
            Usuario u = usuarioValido();
            u.setNombre("Juan123");

            assertThrows(IllegalArgumentException.class,
                    () -> service.registrarUsuario(u));
            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("Username inválido (empieza con número) → excepción")
        void registrar_usernameInvalido() {
            Usuario u = usuarioValido();
            u.setUsername("1juan");

            assertThrows(IllegalArgumentException.class,
                    () -> service.registrarUsuario(u));
            verifyNoInteractions(repository);
        }
    }

    @Nested
    @DisplayName("Suite – buscarPorUsername (lowerCase/trim + Mock)")
    class Suite16_BuscarPorUsername {

        @Test
        @DisplayName("Username con mayúsculas → busca en minúsculas en repositorio")
        void buscar_normalizaAntesDeBuscar() {
            Usuario u = Usuario.builder().id(1L).username("adminuser").build();
            when(repository.findByUsername("adminuser")).thenReturn(Optional.of(u));

            Optional<Usuario> resultado = service.buscarPorUsername("  ADMINUSER  ");

            assertTrue(resultado.isPresent());
            verify(repository).findByUsername("adminuser");
        }

        @Test
        @DisplayName("Username no encontrado → Optional vacío")
        void buscar_noEncontrado() {
            when(repository.findByUsername(anyString())).thenReturn(Optional.empty());

            Optional<Usuario> resultado = service.buscarPorUsername("fantasma");

            assertTrue(resultado.isEmpty());
            verify(repository).findByUsername("fantasma");
        }

        @Test
        @DisplayName("Username null → Optional vacío sin consultar repositorio")
        void buscar_null() {
            Optional<Usuario> resultado = service.buscarPorUsername(null);
            assertTrue(resultado.isEmpty());
            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("Username en blanco → Optional vacío sin consultar repositorio")
        void buscar_blank() {
            Optional<Usuario> resultado = service.buscarPorUsername("   ");
            assertTrue(resultado.isEmpty());
            verifyNoInteractions(repository);
        }
    }

    @Nested
    @DisplayName("Suite – generarPerfilPublico (join/upperCase/leftPad/abbreviate)")
    class Suite17_PerfilPublico {

        @Test
        @DisplayName("Perfil generado contiene ID formateado, nombre en mayúsculas y bio")
        void perfil_completo() {
            Usuario u = Usuario.builder()
                    .id(7L)
                    .nombre("ana")
                    .apellido("gomez")
                    .biografia("Ingeniera de software especializada en backend")
                    .build();

            String perfil = service.generarPerfilPublico(u);

            // StringUtils.leftPad → "00000007"
            assertTrue(perfil.contains("00000007"));
            // StringUtils.upperCase + join → "ANA GOMEZ"
            assertTrue(perfil.contains("ANA GOMEZ"));
            // bio corta → no abreviada
            assertTrue(perfil.contains("Ingeniera de software"));
            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("Perfil sin bio → muestra 'Sin biografía'")
        void perfil_sinBio() {
            Usuario u = Usuario.builder()
                    .id(3L)
                    .nombre("luis")
                    .apellido("rios")
                    .biografia(null)
                    .build();

            String perfil = service.generarPerfilPublico(u);
            assertTrue(perfil.contains("Sin biografía"));
            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("Bio larga → abreviada a ≤ 50 chars en el perfil")
        void perfil_bioLarga() {
            Usuario u = Usuario.builder()
                    .id(99L)
                    .nombre("maria")
                    .apellido("castro")
                    .biografia("Esta es una biografía extremadamente larga que con certeza supera el límite de cincuenta caracteres establecido por la empresa")
                    .build();

            String perfil = service.generarPerfilPublico(u);
            // Extraer la parte de bio
            String bioEnPerfil = perfil.split("BIO: ")[1];
            assertTrue(bioEnPerfil.length() <= 50);
            verifyNoInteractions(repository);
        }
    }
}