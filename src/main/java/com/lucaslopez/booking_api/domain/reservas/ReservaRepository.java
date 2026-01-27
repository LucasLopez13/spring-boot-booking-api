package com.lucaslopez.booking_api.domain.reservas;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    @Query("""
    SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
    FROM Reserva r
    WHERE r.prestacion.id = :idPrestacion
    AND r.estado = 'RESERVADO'
    AND r.fechaDeInicio < :fechaFin 
    AND r.fechaDeFin > :fechaInicio
    """)
    boolean existsSolapamiento(
            @Param("idPrestacion") Long idPrestacion,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    Page<Reserva> findByUsuarioId(Long usuarioId, Pageable pageable);
}
