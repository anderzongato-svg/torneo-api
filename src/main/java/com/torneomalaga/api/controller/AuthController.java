package com.torneomalaga.api.controller;

import com.torneomalaga.api.security.JwtUtil;
import com.torneomalaga.api.security.dto.LoginRequest;
import com.torneomalaga.api.security.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de autenticación de torneo-api.
 *
 * Expone el único endpoint público de login. Las credenciales del
 * administrador se configuran externamente en application.properties
 * (admin.usuario, admin.contrasena) — no van hardcodeadas en el código.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${admin.usuario}")
    private String adminUsuario;

    @Value("${admin.contrasena}")
    private String adminContrasena;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (request.getUsuario() == null || request.getContrasena() == null) {
            return ResponseEntity.badRequest().body("Usuario y contraseña son obligatorios");
        }

        boolean credencialesValidas = adminUsuario.equals(request.getUsuario())
                && adminContrasena.equals(request.getContrasena());

        if (!credencialesValidas) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }

        String token = jwtUtil.generarToken(request.getUsuario());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
