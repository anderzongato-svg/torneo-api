package com.torneomalaga.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Módulo de autenticación (parte 2/3): MIDDLEWARE de la API.
 *
 * Este es el componente que exigía la guía: un filtro que se ejecuta UNA VEZ
 * por cada petición HTTP entrante, ANTES de que llegue a cualquier
 * Controller. Su responsabilidad es:
 *   1) Leer el header "Authorization: Bearer <token>".
 *   2) Validar el token JWT contra JwtUtil.
 *   3) Si es válido, autenticar la petición en el contexto de seguridad de
 *      Spring, para que SecurityConfig deje pasar la solicitud.
 *   4) Si no hay token o es inválido, simplemente no autentica: Spring
 *      Security se encargará de rechazar la petición con 401/403 si la ruta
 *      está protegida.
 *
 * Se registra en la cadena de filtros de Spring Security en SecurityConfig.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String usuario = null;
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                usuario = jwtUtil.extraerUsuario(token);
            } catch (Exception e) {
                // Token malformado o expirado: se deja usuario en null,
                // la petición seguirá sin autenticar.
                logger.warn("Token JWT inválido: " + e.getMessage());
            }
        }

        if (usuario != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.esTokenValido(token, usuario)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(usuario, null, Collections.emptyList());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
