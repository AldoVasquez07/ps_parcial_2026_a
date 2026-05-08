package pruebas.software.parcial.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class TemplateProcessorService {

    /**
     * Procesa una plantilla reemplazando múltiples etiquetas.
     * Usa StringUtils.replaceEach para mayor eficiencia.
     */
    public String processTemplate(String template, String name, String id) {
        if (StringUtils.isBlank(template)) {
            return "";
        }
        
        String[] searchList = {"${name}", "${id}"};
        String[] replacementList = {
            StringUtils.defaultString(name, "Invitado"), 
            StringUtils.defaultString(id, "000")
        };
        
        return StringUtils.replaceEach(template, searchList, replacementList);
    }

    /**
     * Extrae y limpia un código oculto entre delimitadores.
     * Demuestra el uso de substringBetween y trim.
     */
    public String extractAndCleanCode(String data) {
        String extracted = StringUtils.substringBetween(data, "[[", "]]");
        return StringUtils.trimToEmpty(extracted);
    }

    /**
     * Verifica si una cadena es un "Palíndromo" simplificado (ignorando espacios y mayúsculas).
     */
    public boolean isSimplePalindrome(String text) {
        if (StringUtils.isBlank(text)) return false;
        String clean = StringUtils.deleteWhitespace(text).toLowerCase();
        String reversed = StringUtils.reverse(clean);
        return StringUtils.equals(clean, reversed);
    }
}