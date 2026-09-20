package com.torneomalaga.api.security.dto;

/**
 * Cuerpo esperado por POST /api/auth/login.
 */
public class LoginRequest {

    private String usuario;
    private String contrasena;

    public LoginRequest() {
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
