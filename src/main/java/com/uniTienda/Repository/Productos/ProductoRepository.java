package com.uniTienda.Repository.Productos;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uniTienda.Model.Productos.Producto;


public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByNombreContaining(String nombre);
// Consulta personalizada para buscar productos que contengan un color específico en el array `colores`

    // Filtro por rango de precios
    @Query("SELECT p FROM Producto p WHERE p.precio BETWEEN :precioMin AND :precioMax")
    List<Producto> findByPrecioBetween(@Param("precioMin") BigDecimal precioMin, @Param("precioMax") BigDecimal precioMax);

    // Filtro para productos con descuento disponible
    @Query("SELECT p FROM Producto p WHERE p.descuento > 0.0")
    List<Producto> findWithDescuento();


     // Consulta nativa para buscar productos que contengan al menos uno de los colores especificados en el array `colores`
     @Query(value = "SELECT * FROM producto p WHERE p.colores && :colores", nativeQuery = true)
     List<Producto> findByColoresIn(@Param("colores") String[] colores);
 
}