package co.edu.usco.petcotas.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de prueba — sirve para verificar si JWT funciona correctamente.
 * - /api/test/public  -> accesible sin token.
 * - /api/test/private -> requiere token JWT válido.
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    // ✅ Endpoint público: no requiere autenticación
    @GetMapping("/public")
    public String publicEndpoint() {
        return "✅ Endpoint público: no se requiere token.";
    }

    // 🔒 Endpoint protegido: requiere un JWT válido
    @GetMapping("/private")
    public String privateEndpoint() {
        return "🔐 Accediste al endpoint privado con un token JWT válido.";
    }
}
