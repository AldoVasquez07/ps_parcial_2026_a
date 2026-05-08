package pruebas.software.parcial.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pruebas.software.parcial.service.TemplateProcessorService;

import java.util.Map;

@RestController
@RequestMapping("/api/string-utils")
public class StringUtilsController {

    @Autowired
    private TemplateProcessorService templateService;

    /**
     * Endpoint para formatear nombres: quita acentos, espacios y capitaliza.
     * Ejemplo: "  ángel  " -> "Angel"
     */
    @GetMapping("/format-name")
    public String formatName(@RequestParam String name) {
        String step1 = StringUtils.trimToEmpty(name);
        String step2 = StringUtils.stripAccents(step1);
        return StringUtils.capitalize(step2.toLowerCase());
    }

    /**
     * Endpoint para procesar una plantilla mediante POST.
     */
    @PostMapping("/process-template")
    public String process(@RequestBody Map<String, String> payload) {
        String template = payload.getOrDefault("template", "");
        String name = payload.get("name");
        String id = payload.get("id");
        
        return templateService.processTemplate(template, name, id);
    }

    /**
     * Endpoint que utiliza lógica de comparación compleja.
     * Devuelve la diferencia entre dos textos.
     */
    @GetMapping("/compare")
    public String compare(@RequestParam String s1, @RequestParam String s2) {
        // Devuelve la parte donde s2 empieza a diferir de s1
        return StringUtils.difference(s1, s2);
    }
}