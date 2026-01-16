package com.lucaslopez.booking_api.domain.prestaciones;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PrestacionRepository extends JpaRepository<Prestacion, Long> {
    boolean existsByNombre(String nombre);
}
