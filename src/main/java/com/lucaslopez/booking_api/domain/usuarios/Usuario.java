package com.lucaslopez.booking_api.domain.usuarios;

import com.lucaslopez.booking_api.domain.reservas.Reserva;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Table(name = "usuarios")
@Entity(name = "Usuario")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
//Estrategia de Soft delete.
// Intercepta cualquier intento de borrado físico (repository.delete) y ejecuta un UPDATE.
@SQLDelete(sql = "UPDATE usuarios SET activo = false WHERE id = ?")
//Aplica automáticamente un filtro "WHERE activo = true" a todas las consultas de lectura (SELECT).
@SQLRestriction("activo = true")
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    @Column(unique = true)
    private String email;
    private String contrasenia;
    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean activo;
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Reserva> reservas = new ArrayList<>();


    public Usuario(DatosRegistroUsuario datos, String contraseniaEncriptada) {
        this.id = null;
        this.nombre = datos.nombre();
        this.email = datos.email();
        this.contrasenia = contraseniaEncriptada;
        this.role = Role.USER;
        this.activo = true;
    }

    public void actualizarUsuario(DatosActualizacionUsuario datos) {
        if (datos.nombre() != null) {
            this.nombre = datos.nombre();
        }
        if (datos.email() != null) {
            this.email = datos.email();
        }
    }

    public void agregarReserva(Reserva reserva) {
        this.reservas.add(reserva);
    }

    public void asignarRole(Role role) {
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return contrasenia;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
