package com.uniTienda.Repository.Orden;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uniTienda.Model.Orden.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
}