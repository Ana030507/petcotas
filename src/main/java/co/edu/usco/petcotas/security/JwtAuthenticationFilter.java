package co.edu.usco.petcotas.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que se ejecuta una vez por cada petición HTTP.
 * - Extrae el token JWT del encabezado "Authorization".
 * - Valida el token y establece la autenticación en el contexto de seguridad.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        log.debug("🔍 JwtAuthenticationFilter: procesando {}", request.getRequestURI());

        // Leer encabezado Authorization
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Si no hay token o no empieza con "Bearer ", no hacemos nada
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("⚠️ No hay token Bearer en la petición");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extraer el token (sin "Bearer ")
            jwt = authHeader.substring(7);
            log.debug("✅ Token extraído: {}...", jwt.substring(0, Math.min(20, jwt.length())));

            username = jwtService.extractUsername(jwt);
            log.debug("📧 Username extraído del token: {}", username);

            // Si hay usuario y no hay autenticación activa aún
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                log.debug("👤 UserDetails cargado para: {} con authorities: {}", username, userDetails.getAuthorities());

                // Si el token es válido, configuramos la autenticación
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("🔐 Autenticación establecida para: {} con authorities: {}", username, userDetails.getAuthorities());
                } else {
                    log.warn("❌ Token inválido para el usuario: {}", username);
                }
            } else if (username == null) {
                log.warn("❌ No se pudo extraer username del token");
            }
        } catch (Exception e) {
            log.error("💥 Error procesando JWT: {}", e.getMessage(), e);
        }

        // Continuar con el siguiente filtro de la cadena
        filterChain.doFilter(request, response);
    }
}
