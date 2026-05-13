package pruebas.software.parcial.stringutilstest;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StringUtils con JUnit")
class StringUtilsJUnitTest {

    @Nested
    @DisplayName("Pruebas con: isBlank / isNotBlank")
    class Suite1_Blank {

        @Test
        @DisplayName("isBlank(null) → true")
        void isBlank_null() {
            assertTrue(StringUtils.isBlank(null));
        }

        @Test
        @DisplayName("isBlank(\"\") → true")
        void isBlank_empty() {
            assertTrue(StringUtils.isBlank(""));
        }

        @Test
        @DisplayName("isBlank(\"   \") → true")
        void isBlank_spaces() {
            assertTrue(StringUtils.isBlank("   "));
        }

        @Test
        @DisplayName("isBlank(\"hola\") → false")
        void isBlank_text() {
            assertFalse(StringUtils.isBlank("hola"));
        }

        @Test
        @DisplayName("isBlank(\"  texto  \") → false")
        void isBlank_textWithSpaces() {
            assertFalse(StringUtils.isBlank("  texto  "));
        }

        @ParameterizedTest(name = "isNotBlank(\"{0}\") → false")
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        @DisplayName("isNotBlank: null / vacío / solo blancos → false")
        void isNotBlank_falsyValues(String input) {
            assertFalse(StringUtils.isNotBlank(input));
        }

        @Test
        @DisplayName("isNotBlank(\"contenido\") → true")
        void isNotBlank_text() {
            assertTrue(StringUtils.isNotBlank("contenido"));
        }
    }

    @Nested
    @DisplayName("Pruebas con: isEmpty / isNotEmpty")
    class Suite2_Empty {

        @Test
        @DisplayName("isEmpty(null) → true")
        void isEmpty_null() {
            assertTrue(StringUtils.isEmpty(null));
        }

        @Test
        @DisplayName("isEmpty(\"\") → true")
        void isEmpty_emptyString() {
            assertTrue(StringUtils.isEmpty(""));
        }

        @Test
        @DisplayName("isEmpty(\" \") → false  (diferencia con isBlank)")
        void isEmpty_space() {
            assertFalse(StringUtils.isEmpty(" "));
        }

        @Test
        @DisplayName("isEmpty(\"abc\") → false")
        void isEmpty_text() {
            assertFalse(StringUtils.isEmpty("abc"));
        }

        @Test
        @DisplayName("isNotEmpty(\"\") → false")
        void isNotEmpty_empty() {
            assertFalse(StringUtils.isNotEmpty(""));
        }

        @Test
        @DisplayName("isNotEmpty(\" \") → true")
        void isNotEmpty_space() {
            assertTrue(StringUtils.isNotEmpty(" "));
        }

        @Test
        @DisplayName("isNotEmpty(\"texto\") → true")
        void isNotEmpty_text() {
            assertTrue(StringUtils.isNotEmpty("texto"));
        }
    }

    @Nested
    @DisplayName("Pruebas con: capitalize / upperCase / lowerCase")
    class Suite3_Case {
        @Test
        @DisplayName("capitalize(\"hola mundo\") → \"Hola mundo\"")
        void capitalize_basic() {
            assertEquals("Hola mundo", StringUtils.capitalize("hola mundo"));
        }

        @Test
        @DisplayName("capitalize: primera ya mayúscula → sin cambio")
        void capitalize_alreadyCap() {
            assertEquals("Admin", StringUtils.capitalize("Admin"));
        }

        @Test
        @DisplayName("capitalize(null) → null")
        void capitalize_null() {
            assertNull(StringUtils.capitalize(null));
        }

        @Test
        @DisplayName("capitalize(\"\") → \"\"")
        void capitalize_empty() {
            assertEquals("", StringUtils.capitalize(""));
        }

        @Test
        @DisplayName("upperCase(\"apache commons\") → \"APACHE COMMONS\"")
        void upperCase_basic() {
            assertEquals("APACHE COMMONS", StringUtils.upperCase("apache commons"));
        }

        @Test
        @DisplayName("upperCase(null) → null")
        void upperCase_null() {
            assertNull(StringUtils.upperCase(null));
        }

        @Test
        @DisplayName("lowerCase(\"JAVA 17\") → \"java 17\"")
        void lowerCase_basic() {
            assertEquals("java 17", StringUtils.lowerCase("JAVA 17"));
        }

        @Test
        @DisplayName("lowerCase(null) → null")
        void lowerCase_null() {
            assertNull(StringUtils.lowerCase(null));
        }

        @Test
        @DisplayName("upperCase + lowerCase son operaciones inversas")
        void caseRoundTrip() {
            String original = "SpringBoot";
            assertEquals(original.toLowerCase(),
                    StringUtils.lowerCase(StringUtils.upperCase(original)));
        }
    }

    @Nested
    @DisplayName("Pruebas: trim / strip")
    class Suite4_TrimStrip {

        @Test
        @DisplayName("trim(\"  hola  \") → \"hola\"")
        void trim_spaces() {
            assertEquals("hola", StringUtils.trim("  hola  "));
        }

        @Test
        @DisplayName("trim(null) → null")
        void trim_null() {
            assertNull(StringUtils.trim(null));
        }

        @Test
        @DisplayName("trim(\"\") → \"\"")
        void trim_empty() {
            assertEquals("", StringUtils.trim(""));
        }

        @Test
        @DisplayName("strip elimina tabulaciones y saltos de línea")
        void strip_tabs() {
            assertEquals("texto", StringUtils.strip("\t texto \n"));
        }

        @Test
        @DisplayName("strip(null) → null")
        void strip_null() {
            assertNull(StringUtils.strip(null));
        }

        @Test
        @DisplayName("trim y strip producen el mismo resultado para espacios ASCII")
        void trimEqualsStrip() {
            String input = "  igual  ";
            assertEquals(StringUtils.trim(input), StringUtils.strip(input));
        }
    }

    @Nested
    @DisplayName("Pruebas con: truncate / abbreviate")
    class Suite5_TruncAbbrev {
        @Test
        @DisplayName("truncate: recorta al ancho exacto")
        void truncate_basic() {
            assertEquals("Hola", StringUtils.truncate("Hola Mundo", 4));
        }

        @Test
        @DisplayName("truncate: cadena más corta → sin cambio")
        void truncate_shorter() {
            assertEquals("Hi", StringUtils.truncate("Hi", 10));
        }

        @Test
        @DisplayName("truncate(null, 5) → null")
        void truncate_null() {
            assertNull(StringUtils.truncate(null, 5));
        }

        @Test
        @DisplayName("truncate: ancho = 0 → cadena vacía")
        void truncate_zero() {
            assertEquals("", StringUtils.truncate("Texto", 0));
        }

        @Test
        @DisplayName("abbreviate: agrega '...' al truncar")
        void abbreviate_basic() {
            String result = StringUtils.abbreviate("Esta es una descripción muy larga para el test", 20);
            assertTrue(result.endsWith("..."));
            assertEquals(20, result.length());
        }

        @Test
        @DisplayName("abbreviate: cadena corta → sin cambio")
        void abbreviate_shorter() {
            assertEquals("Hola", StringUtils.abbreviate("Hola", 10));
        }

        @Test
        @DisplayName("abbreviate(null, n) → null")
        void abbreviate_null() {
            assertNull(StringUtils.abbreviate(null, 10));
        }

        @Test
        @DisplayName("abbreviate: width < 4 lanza IllegalArgumentException")
        void abbreviate_tooShort() {
            assertThrows(IllegalArgumentException.class,
                    () -> StringUtils.abbreviate("Texto largo aquí", 3));
        }
    }

    @Nested
    @DisplayName("Pruebas con: contains / containsIgnoreCase")
    class Suite6_Contains {

        @Test
        @DisplayName("contains: subcadena presente → true")
        void contains_found() {
            assertTrue(StringUtils.contains("spring boot", "boot"));
        }

        @Test
        @DisplayName("contains: subcadena ausente → false")
        void contains_notFound() {
            assertFalse(StringUtils.contains("spring boot", "cloud"));
        }

        @Test
        @DisplayName("contains: null en la cadena fuente → false")
        void contains_nullSource() {
            assertFalse(StringUtils.contains(null, "boot"));
        }

        @Test
        @DisplayName("contains: null como búsqueda → false")
        void contains_nullSearch() {
            assertFalse(StringUtils.contains("texto", null));
        }

        @Test
        @DisplayName("containsIgnoreCase: diferente caja → true")
        void containsIgnoreCase_true() {
            assertTrue(StringUtils.containsIgnoreCase("Apache Commons", "COMMONS"));
        }

        @Test
        @DisplayName("containsIgnoreCase: no está presente → false")
        void containsIgnoreCase_false() {
            assertFalse(StringUtils.containsIgnoreCase("Java", "Python"));
        }
    }

    @Nested
    @DisplayName("Pruebas con: startsWith / endsWith")
    class Suite7_StartsEnds {

        @Test
        @DisplayName("startsWith: prefijo correcto → true")
        void startsWith_true() {
            assertTrue(StringUtils.startsWith("usuario_admin", "usuario"));
        }

        @Test
        @DisplayName("startsWith: prefijo incorrecto → false")
        void startsWith_false() {
            assertFalse(StringUtils.startsWith("usuario_admin", "admin"));
        }

        @Test
        @DisplayName("startsWith(null, prefix) → false")
        void startsWith_null() {
            assertFalse(StringUtils.startsWith(null, "pre"));
        }

        @Test
        @DisplayName("startsWithIgnoreCase → true aunque caja diferente")
        void startsWithIgnoreCase_true() {
            assertTrue(StringUtils.startsWithIgnoreCase("Admin_01", "admin"));
        }

        @Test
        @DisplayName("endsWith: sufijo correcto → true")
        void endsWith_true() {
            assertTrue(StringUtils.endsWith("archivo.pdf", ".pdf"));
        }

        @Test
        @DisplayName("endsWith: sufijo incorrecto → false")
        void endsWith_false() {
            assertFalse(StringUtils.endsWith("archivo.pdf", ".docx"));
        }

        @Test
        @DisplayName("endsWithIgnoreCase → true aunque caja diferente")
        void endsWithIgnoreCase_true() {
            assertTrue(StringUtils.endsWithIgnoreCase("Imagen.PNG", ".png"));
        }
    }

    @Nested
    @DisplayName("Pruebas con reverse")
    class Suite8_Reverse {

        @Test
        @DisplayName("reverse(\"java\") → \"avaj\"")
        void reverse_basic() {
            assertEquals("avaj", StringUtils.reverse("java"));
        }

        @Test
        @DisplayName("reverse: palíndromo → igual")
        void reverse_palindrome() {
            assertEquals("radar", StringUtils.reverse("radar"));
        }

        @Test
        @DisplayName("reverse(null) → null")
        void reverse_null() {
            assertNull(StringUtils.reverse(null));
        }

        @Test
        @DisplayName("reverse(\"\") → \"\"")
        void reverse_empty() {
            assertEquals("", StringUtils.reverse(""));
        }

        @Test
        @DisplayName("reverse(reverse(x)) == x  (idempotencia doble)")
        void reverse_doubleReverse() {
            String original = "SpringBoot";
            assertEquals(original, StringUtils.reverse(StringUtils.reverse(original)));
        }
    }

    @Nested
    @DisplayName("Pruebas con: leftPad / rightPad")
    class Suite9_Pad {

        @Test
        @DisplayName("leftPad(\"42\", 5, '0') → \"00042\"")
        void leftPad_zeros() {
            assertEquals("00042", StringUtils.leftPad("42", 5, '0'));
        }

        @Test
        @DisplayName("leftPad: cadena ya del tamaño → sin cambio")
        void leftPad_exact() {
            assertEquals("12345", StringUtils.leftPad("12345", 5, '0'));
        }

        @Test
        @DisplayName("leftPad: cadena más larga que el ancho → sin recorte")
        void leftPad_longer() {
            assertEquals("123456", StringUtils.leftPad("123456", 3, '0'));
        }

        @Test
        @DisplayName("leftPad(null, 5, '0') → null")
        void leftPad_null() {
            assertNull(StringUtils.leftPad(null, 5, '0'));
        }

        @Test
        @DisplayName("rightPad(\"Hola\", 6, ' ') → \"Hola  \"")
        void rightPad_spaces() {
            assertEquals("Hola  ", StringUtils.rightPad("Hola", 6, ' '));
        }

        @Test
        @DisplayName("rightPad(\"abc\", 5, '-') → \"abc--\"")
        void rightPad_dash() {
            assertEquals("abc--", StringUtils.rightPad("abc", 5, '-'));
        }

        @Test
        @DisplayName("rightPad(null, 3, ' ') → null")
        void rightPad_null() {
            assertNull(StringUtils.rightPad(null, 3, ' '));
        }
    }

    @Nested
    @DisplayName("Pruebas con: countMatches")
    class Suite10_CountMatches {

        @Test
        @DisplayName("countMatches(\"banana\", \"a\") → 3")
        void countMatches_multiple() {
            assertEquals(3, StringUtils.countMatches("banana", "a"));
        }

        @Test
        @DisplayName("countMatches: sin coincidencia → 0")
        void countMatches_none() {
            assertEquals(0, StringUtils.countMatches("banana", "z"));
        }

        @Test
        @DisplayName("countMatches(null, \"a\") → 0")
        void countMatches_null() {
            assertEquals(0, StringUtils.countMatches(null, "a"));
        }

        @ParameterizedTest(name = "countMatches(\"{0}\", \"{1}\") → {2}")
        @CsvSource({"abcabc,abc,2", "aaaa,aa,2", "java,a,2"})
        @DisplayName("countMatches: tabla de casos")
        void countMatches_table(String str, String sub, int expected) {
            assertEquals(expected, StringUtils.countMatches(str, sub));
        }
    }

    @Nested
    @DisplayName("Pruebas con: replace")
    class Suite11_Replace {

        @Test
        @DisplayName("replace: sustituye todas las ocurrencias")
        void replace_all() {
            assertEquals("Hell* W*rld", StringUtils.replace("Hello World", "o", "*"));
        }

        @Test
        @DisplayName("replace: sin ocurrencia → texto original sin cambio")
        void replace_none() {
            assertEquals("Hola", StringUtils.replace("Hola", "z", "X"));
        }

        @Test
        @DisplayName("replace(null, ...) → null")
        void replace_null() {
            assertNull(StringUtils.replace(null, "a", "b"));
        }

        @Test
        @DisplayName("replace: reemplaza por cadena vacía (elimina)")
        void replace_deletePattern() {
            assertEquals("Hell Wrld", StringUtils.replace("Hello World", "o", ""));
        }
    }

    @Nested
    @DisplayName("Pruebas con: equals / equalsIgnoreCase")
    class Suite12_Equals {

        @Test
        @DisplayName("equals: mismas cadenas → true")
        void equals_same() {
            assertTrue(StringUtils.equals("admin", "admin"));
        }

        @Test
        @DisplayName("equals: distinta caja → false  (case-sensitive)")
        void equals_caseSensitive() {
            assertFalse(StringUtils.equals("Admin", "admin"));
        }

        @Test
        @DisplayName("equals(null, null) → true  (null-safe)")
        void equals_bothNull() {
            assertTrue(StringUtils.equals(null, null));
        }

        @Test
        @DisplayName("equals(null, \"texto\") → false  (null-safe)")
        void equals_nullVsText() {
            assertFalse(StringUtils.equals(null, "texto"));
        }

        @Test
        @DisplayName("equalsIgnoreCase(\"ADMIN\", \"admin\") → true")
        void equalsIgnoreCase_true() {
            assertTrue(StringUtils.equalsIgnoreCase("ADMIN", "admin"));
        }

        @Test
        @DisplayName("equalsIgnoreCase(\"abc\", \"xyz\") → false")
        void equalsIgnoreCase_false() {
            assertFalse(StringUtils.equalsIgnoreCase("abc", "xyz"));
        }
    }

    @Nested
    @DisplayName("Pruebas con: isAlpha / isNumeric / isAlphanumeric")
    class Suite13_CharTypes {

        @Test
        @DisplayName("isAlpha(\"AbcDef\") → true")
        void isAlpha_true() {
            assertTrue(StringUtils.isAlpha("AbcDef"));
        }

        @Test
        @DisplayName("isAlpha: con dígito → false")
        void isAlpha_withDigit() {
            assertFalse(StringUtils.isAlpha("abc1"));
        }

        @Test
        @DisplayName("isAlpha(null) → false")
        void isAlpha_null() {
            assertFalse(StringUtils.isAlpha(null));
        }

        @ParameterizedTest(name = "isAlpha(\"{0}\") → {1}")
        @CsvSource({"Juan,true", "Juan123,false", "'  ',false", "'',false"})
        @DisplayName("isAlpha: tabla de parámetros")
        void isAlpha_table(String input, boolean expected) {
            assertEquals(expected, StringUtils.isAlpha(input));
        }

        @Test
        @DisplayName("isNumeric(\"123456\") → true")
        void isNumeric_true() {
            assertTrue(StringUtils.isNumeric("123456"));
        }

        @Test
        @DisplayName("isNumeric: con letra → false")
        void isNumeric_withLetter() {
            assertFalse(StringUtils.isNumeric("123a56"));
        }

        @Test
        @DisplayName("isNumeric(null) → false")
        void isNumeric_null() {
            assertFalse(StringUtils.isNumeric(null));
        }

        @Test
        @DisplayName("isAlphanumeric(\"abc123\") → true")
        void isAlphanumeric_true() {
            assertTrue(StringUtils.isAlphanumeric("abc123"));
        }

        @Test
        @DisplayName("isAlphanumeric: con espacio → false")
        void isAlphanumeric_space() {
            assertFalse(StringUtils.isAlphanumeric("abc 123"));
        }
    }

    @Nested
    @DisplayName("Pruebas con: join")
    class Suite14_Join {

        @Test
        @DisplayName("join: une array con coma")
        void join_comma() {
            assertEquals("a,b,c", StringUtils.join(new String[]{"a", "b", "c"}, ","));
        }

        @Test
        @DisplayName("join: un solo elemento → sin separador")
        void join_single() {
            assertEquals("solo", StringUtils.join(new String[]{"solo"}, "-"));
        }

        @Test
        @DisplayName("join: array vacío → cadena vacía")
        void join_emptyArray() {
            assertEquals("", StringUtils.join(new String[]{}, ","));
        }

        @Test
        @DisplayName("join: elemento null → representado como 'null'")
        void join_withNullElement() {
            assertEquals("a--b", StringUtils.join(new String[]{"a", null, "b"}, "-"));
        }

        @Test
        @DisplayName("join: separador vacío → concatenación directa")
        void join_emptySeparator() {
            assertEquals("abc", StringUtils.join(new String[]{"a", "b", "c"}, ""));
        }
    }

    @Nested
    @DisplayName("Pruebas con: repeat")
    class Suite15_Repeat {

        @Test
        @DisplayName("repeat(\"ab\", 3) → \"ababab\"")
        void repeat_basic() {
            assertEquals("ababab", StringUtils.repeat("ab", 3));
        }

        @Test
        @DisplayName("repeat(\"x\", 0) → \"\"")
        void repeat_zero() {
            assertEquals("", StringUtils.repeat("x", 0));
        }

        @Test
        @DisplayName("repeat(null, 3) → null")
        void repeat_null() {
            assertNull(StringUtils.repeat(null, 3));
        }

        @Test
        @DisplayName("repeat con separador: repeat(\"ha\", \",\", 3) → \"ha,ha,ha\"")
        void repeat_withSeparator() {
            assertEquals("ha,ha,ha", StringUtils.repeat("ha", ",", 3));
        }
    }
}