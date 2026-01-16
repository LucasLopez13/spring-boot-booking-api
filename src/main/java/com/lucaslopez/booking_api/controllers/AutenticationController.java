package com.lucaslopez.booking_api.controllers;

import com.lucaslopez.booking_api.domain.usuarios.DatosAutenticacionUsuario;
import com.lucaslopez.booking_api.domain.usuarios.Usuario;
import com.lucaslopez.booking_api.infra.security.DatosTokenJWT;
import com.lucaslopez.booking_api.infra.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AutenticationController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;


    @PostMapping
    public ResponseEntity inicarSesion(@RequestBody @Valid DatosAutenticacionUsuario datos){
        var autenticacionToken = new UsernamePasswordAuthenticationToken(datos.email(),datos.contrasenia());
        var autenticacion = authenticationManager.authenticate(autenticacionToken);

        var tokenJWT = tokenService.generarToken((Usuario) autenticacion.getPrincipal());

        return ResponseEntity.ok(new DatosTokenJWT(tokenJWT));
    }

}
