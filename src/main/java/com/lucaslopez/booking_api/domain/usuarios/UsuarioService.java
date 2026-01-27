package com.lucaslopez.booking_api.domain.usuarios;

import com.lucaslopez.booking_api.domain.ValidacionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario registrarUsuario(DatosRegistroUsuario datos) {
        validarEmailUnico(datos.email());

        if (datos.nombre() == null || datos.nombre().isEmpty()) {
            throw new ValidacionException("Debe ingresar un nombre valido");
        }

        if (datos.contrasenia() == null || datos.contrasenia().isEmpty()) {
            throw new ValidacionException("Debe ingresar una contrasenia valida");
        }

        var passwordHash = passwordEncoder.encode(datos.contrasenia());
        var usuario = new Usuario(datos, passwordHash);

        return usuarioRepository.save(usuario);
    }

    public DatosDetalleUsuario detallarUsuario(Long id) {
        var usuario = buscarUsuarioPorId(id);

        return new DatosDetalleUsuario(usuario);
    }

    public Usuario actualizarUsuario(Long id,DatosActualizacionUsuario datos) {
        var usuario = buscarUsuarioPorId(id);

        validarEmailEnActualizacion(datos,usuario);

        usuario.actualizarUsuario(datos);

        return usuario;

    }

    public void eliminarUsuario(Long id) {
        var usuario =  buscarUsuarioPorId(id);
        usuarioRepository.deleteById(usuario.getId());
    }

    private void validarEmailUnico(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new ValidacionException("El email ingresado ya se encuentra registrado.");
        }
    }

    private Usuario buscarUsuarioPorId(Long id){
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ValidacionException("Usuario no encontrado"));
    }

    private void validarEmailEnActualizacion(DatosActualizacionUsuario datos, Usuario usuario) {
        if (datos.email() != null && !datos.email().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(datos.email())) {
                throw new ValidacionException("El email ya se encuentra en el sistema");
            }
        }
    }

    public Usuario cambiarRol(Long id, Role role) {
        var usuario =  buscarUsuarioPorId(id);

        usuario.asignarRole(role);

        return usuario;
    }
}
