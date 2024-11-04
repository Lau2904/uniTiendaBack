package com.uniTienda.Repository.CarritoFolder;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uniTienda.Model.CarritoFolder.Carrito;
import com.uniTienda.Model.Usuario;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    Optional<Carrito> findByClienteAndEstado(Usuario cliente, String estado);
    Optional<Carrito> findBySessionIdAndEstado(String sessionId, String estado);
    Optional<Carrito> findByClienteIdAndEstado(Long clienteId, String estado);
}


