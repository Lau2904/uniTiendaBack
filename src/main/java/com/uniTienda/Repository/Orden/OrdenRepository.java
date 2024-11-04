package com.uniTienda.Repository.Orden;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uniTienda.Model.Orden.Orden;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
    List<Orden> findByUsuarioId(Long usuarioId);
}
