package com.uniTienda.Repository.CarritoFolder;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uniTienda.Model.CarritoFolder.Carrito;
import com.uniTienda.Model.CarritoFolder.CarritoDetalle;
import com.uniTienda.Model.Producto;

@Repository
public interface CarritoDetalleRepository extends JpaRepository<CarritoDetalle, Long> {
    
    // Método para encontrar un detalle específico en el carrito
    Optional<CarritoDetalle> findByCarritoAndProducto(Carrito carrito, Producto producto);
    
    // Método para obtener todos los detalles de un carrito específico
    List<CarritoDetalle> findByCarrito(Carrito carrito);
}
