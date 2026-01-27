package com.lucaslopez.booking_api.security;

import com.lucaslopez.booking_api.domain.usuarios.Role;
import com.lucaslopez.booking_api.domain.usuarios.Usuario;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // Creamos el usuario de dominio con ID
        Usuario usuario = Usuario.builder()
                .id(annotation.id())
                .nombre(annotation.username())
                .email(annotation.username() + "booking.com")
                .role(Role.valueOf(annotation.role())).build();

        var auth = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
