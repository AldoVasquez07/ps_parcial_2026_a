package pruebas.software.parcial.stringutilstest;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StringUtils con Mockito")
class StringUtilsMockitoTest {

    interface StringProcessor {
        String procesar(String texto);
        boolean validar(String texto);
        int   contar(String texto, String patron);
        String formatear(String texto, int ancho);
    }

    static class TextoPipeline {
        private final StringProcessor processor;

        TextoPipeline(StringProcessor p) { this.processor = p; }

        String capitalizarSiValido(String texto) {
            if (StringUtils.isBlank(texto)) return StringUtils.EMPTY;
            return processor.procesar(texto);
        }

        String validarYUpperCase(String texto) {
            if (!processor.validar(texto)) return null;
            return StringUtils.upperCase(texto);
        }

        String contarConPrefijo(String texto, String patron) {
            int n = processor.contar(texto, patron);
            return "TOTAL:" + n;
        }

        String formatearSeguro(String texto, int ancho) {
            String resultado = processor.formatear(texto, ancho);
            return StringUtils.defaultString(resultado, "N/A");
        }
    }

    @Mock
    private StringProcessor processorMock;

    @InjectMocks
    private TextoPipeline pipeline;

    @Captor
    private ArgumentCaptor<String> textoCaptor;

    @BeforeEach
    void setUp() {
        pipeline = new TextoPipeline(processorMock);
    }

    @Nested
    @DisplayName("Pruebas con: isBlank como guarda (mock nunca invocado)")
    class Suite1_BlankGuard {

        @Test
        @DisplayName("Texto null → devuelve vacío sin llamar al procesador")
        void null_noInteraction() {
            String resultado = pipeline.capitalizarSiValido(null);
            assertEquals("", resultado);
            verifyNoInteractions(processorMock);
        }

        @Test
        @DisplayName("Texto en blanco → devuelve vacío sin llamar al procesador")
        void blank_noInteraction() {
            String resultado = pipeline.capitalizarSiValido("   ");
            assertEquals("", resultado);
            verifyNoInteractions(processorMock);
        }

        @Test
        @DisplayName("Texto válido → procesador invocado exactamente una vez")
        void valid_processorCalledOnce() {
            when(processorMock.procesar("hola")).thenReturn("Hola");
            String resultado = pipeline.capitalizarSiValido("hola");
            assertEquals("Hola", resultado);
            verify(processorMock, times(1)).procesar("hola");
        }
    }

    @Nested
    @DisplayName("Pruebas con: validar + upperCase (mock controla flujo)")
    class Suite2_ValidarUpperCase {

        @Test
        @DisplayName("Validación true → resultado en mayúsculas (StringUtils.upperCase)")
        void validacion_true_upperCase() {
            when(processorMock.validar("admin")).thenReturn(true);
            String resultado = pipeline.validarYUpperCase("admin");
            assertEquals("ADMIN", resultado);
            verify(processorMock).validar("admin");
        }

        @Test
        @DisplayName("Validación false → retorna null sin aplicar upperCase")
        void validacion_false_null() {
            when(processorMock.validar("inválido")).thenReturn(false);
            String resultado = pipeline.validarYUpperCase("inválido");
            assertNull(resultado);
            verify(processorMock).validar("inválido");
        }

        @Test
        @DisplayName("ArgumentCaptor captura el texto que recibe validar()")
        void captor_capturaTexto() {
            when(processorMock.validar(anyString())).thenReturn(true);
            pipeline.validarYUpperCase("  SpringBoot  ");
            verify(processorMock).validar(textoCaptor.capture());
            assertEquals("  SpringBoot  ", textoCaptor.getValue());
        }
    }

    @Nested
    @DisplayName("Pruebas con: contar con prefijo (mock simula countMatches)")
    class Suite3_ContarPrefijo {

        @Test
        @DisplayName("Mock retorna 3 → resultado es 'TOTAL:3'")
        void contar_treceOcurrencias() {
            when(processorMock.contar("banana", "a")).thenReturn(3);
            assertEquals("TOTAL:3", pipeline.contarConPrefijo("banana", "a"));
            verify(processorMock).contar("banana", "a");
        }

        @Test
        @DisplayName("Mock retorna 0 → resultado es 'TOTAL:0'")
        void contar_cero() {
            when(processorMock.contar("xyz", "a")).thenReturn(0);
            assertEquals("TOTAL:0", pipeline.contarConPrefijo("xyz", "a"));
        }

        @Test
        @DisplayName("Llamadas múltiples → mock retorna valores distintos (thenReturn encadenado)")
        void contar_multipleInvocaciones() {
            when(processorMock.contar(anyString(), eq("a")))
                    .thenReturn(1)
                    .thenReturn(2)
                    .thenReturn(3);

            assertEquals("TOTAL:1", pipeline.contarConPrefijo("texto1", "a"));
            assertEquals("TOTAL:2", pipeline.contarConPrefijo("texto2", "a"));
            assertEquals("TOTAL:3", pipeline.contarConPrefijo("texto3", "a"));
            verify(processorMock, times(3)).contar(anyString(), eq("a"));
        }
    }

    @Nested
    @DisplayName("Pruebas con: formatearSeguro (defaultString ante null del mock)")
    class Suite4_FormatearSeguro {

        @Test
        @DisplayName("Mock retorna valor → pipeline lo entrega tal cual")
        void formatear_valorNormal() {
            when(processorMock.formatear("hola", 8)).thenReturn("00000hola");
            assertEquals("00000hola", pipeline.formatearSeguro("hola", 8));
        }

        @Test
        @DisplayName("Mock retorna null → StringUtils.defaultString produce 'N/A'")
        void formatear_nullDelMock() {
            when(processorMock.formatear(anyString(), anyInt())).thenReturn(null);
            assertEquals("N/A", pipeline.formatearSeguro("cualquier", 5));
        }

        @Test
        @DisplayName("Mock lanza excepción → se propaga correctamente")
        void formatear_excepcionMock() {
            when(processorMock.formatear(anyString(), anyInt()))
                    .thenThrow(new IllegalArgumentException("ancho inválido"));
            assertThrows(IllegalArgumentException.class,
                    () -> pipeline.formatearSeguro("texto", -1));
        }
    }

    @Nested
    @DisplayName("Pruebas con: StringUtils real + mock como spy de verificación")
    class Suite5_StringUtilsReal {
        @Test
        @DisplayName("capitalize vía mock: verifica argumento recibido y resultado")
        void capitalize_viaProxy() {
            when(processorMock.procesar(anyString()))
                    .thenAnswer(inv -> StringUtils.capitalize(inv.getArgument(0)));

            String res = pipeline.capitalizarSiValido("hola mundo");

            assertEquals("Hola mundo", res);
            verify(processorMock).procesar(textoCaptor.capture());
            assertEquals("hola mundo", textoCaptor.getValue());
        }

        @Test
        @DisplayName("lowerCase + trim aplicados antes de llamar al mock")
        void lowerAndTrim_beforeMock() {
            String entrada = "  JAVA  ";
            String normalizada = StringUtils.lowerCase(StringUtils.trim(entrada));

            when(processorMock.validar(normalizada)).thenReturn(true);
            String resultado = pipeline.validarYUpperCase(normalizada);

            assertEquals("JAVA", resultado);
            verify(processorMock).validar("java");
        }

        @Test
        @DisplayName("abbreviate en mock: resultado ≤ 20 chars y termina en '...'")
        void abbreviate_viaProxy() {
            when(processorMock.procesar(anyString()))
                    .thenAnswer(inv -> StringUtils.abbreviate(inv.getArgument(0), 20));

            String bio = "Esta es una biografía muy extensa para el sistema";
            String res = pipeline.capitalizarSiValido(bio);

            assertTrue(res.length() <= 20);
            assertTrue(res.endsWith("..."));
        }

        @Test
        @DisplayName("replace vía mock: censura palabra y verifica llamada")
        void replace_viaProxy() {
            String texto  = "Esto es spam y más spam";
            String censurado = StringUtils.replace(texto, "spam", "***");

            when(processorMock.procesar(texto)).thenReturn(censurado);

            String resultado = pipeline.capitalizarSiValido(texto);
            assertEquals("Esto es *** y más ***", resultado);
            verify(processorMock, times(1)).procesar(texto);
        }

        @Test
        @DisplayName("reverse vía mock: verifica que el resultado es la cadena invertida")
        void reverse_viaProxy() {
            when(processorMock.procesar("apache"))
                    .thenReturn(StringUtils.reverse("apache"));

            String res = pipeline.capitalizarSiValido("apache");
            assertEquals("ehcapa", res);
        }

        @Test
        @DisplayName("leftPad vía formatear: resultado de 8 chars con ceros")
        void leftPad_viaFormatear() {
            when(processorMock.formatear("42", 8))
                    .thenReturn(StringUtils.leftPad("42", 8, '0'));

            String res = pipeline.formatearSeguro("42", 8);
            assertEquals("00000042", res);
            assertEquals(8, res.length());
        }
    }

    @Nested
    @DisplayName("Suite 6 ── Orden e interacciones exactas con el mock")
    class Suite6_Interacciones {

        @Test
        @DisplayName("procesar() llamado 0 veces si texto en blanco (isBlank protege)")
        void cero_invocaciones_si_blank() {
            pipeline.capitalizarSiValido("");
            verify(processorMock, never()).procesar(anyString());
        }

        @Test
        @DisplayName("procesar() llamado exactamente 1 vez por texto válido")
        void una_invocacion_por_texto_valido() {
            when(processorMock.procesar("test")).thenReturn("Test");
            pipeline.capitalizarSiValido("test");
            verify(processorMock, times(1)).procesar("test");
            verifyNoMoreInteractions(processorMock);
        }

        @Test
        @DisplayName("contar() invocado con argumentos exactos capturados por Captor")
        void captor_argumentoContar() {
            ArgumentCaptor<String> texCaptor  = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> patCaptor  = ArgumentCaptor.forClass(String.class);

            when(processorMock.contar(anyString(), anyString())).thenReturn(2);
            pipeline.contarConPrefijo("java java", "java");

            verify(processorMock).contar(texCaptor.capture(), patCaptor.capture());
            assertEquals("java java", texCaptor.getValue());
            assertEquals("java",      patCaptor.getValue());
        }

        @Test
        @DisplayName("InOrder: validar() se llama antes que cualquier otra operación")
        void inOrder_validarPrimero() {
            when(processorMock.validar("dato")).thenReturn(true);

            InOrder orden = inOrder(processorMock);
            pipeline.validarYUpperCase("dato");
            orden.verify(processorMock).validar("dato");
            orden.verifyNoMoreInteractions();
        }

        @Test
        @DisplayName("Spy sobre StringUtils: isAlpha delega correctamente")
        void spy_isAlpha() {
            when(processorMock.validar(anyString()))
                    .thenAnswer(inv -> StringUtils.isAlpha(inv.getArgument(0)));

            assertTrue(pipeline.validarYUpperCase("SoloLetras") != null);
            assertNull(pipeline.validarYUpperCase("Con123"));
        }

        @Test
        @DisplayName("Spy sobre StringUtils: isNumeric delega correctamente")
        void spy_isNumeric() {
            when(processorMock.validar(anyString()))
                    .thenAnswer(inv -> StringUtils.isNumeric(inv.getArgument(0)));

            assertNotNull(pipeline.validarYUpperCase("12345"));
            assertNull(pipeline.validarYUpperCase("123ab"));
        }

        @Test
        @DisplayName("verifyNoMoreInteractions: sin llamadas extras tras procesar")
        void noMoreInteractions_trasProcesar() {
            when(processorMock.procesar("x")).thenReturn("X");
            pipeline.capitalizarSiValido("x");
            verify(processorMock).procesar("x");
            verifyNoMoreInteractions(processorMock);
        }
    }
}