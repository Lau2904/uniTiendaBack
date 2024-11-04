package com.uniTienda.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uniTienda.Model.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    // Obtener pagos por el ID de la orden
    List<Pago> findByOrdenId(Long ordenId);

    // Obtener pagos por el estado
    List<Pago> findByEstado(String estado);

    // Obtener pagos por el método de pago
    List<Pago> findByMetodoPago(String metodoPago);
}
