package com.lucaslopez.booking_api.security;

import org.springframework.security.test.context.support.WithSecurityContext;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Anotación personalizada para Tests de Integración.
 * Proposito: @WithMockUser de Spring Security crea un usuario genérico sin ID.
 * Esta anotación nos permite definir un 'id' específico en el SecurityContext,
 * necesario para probar reglas de autorización como: @PreAuthorize("#id == principal.id")
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
    long id() default 1L;
    String username() default "user";
    String role() default "USER";
}
