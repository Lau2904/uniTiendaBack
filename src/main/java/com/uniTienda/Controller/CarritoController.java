package com.uniTienda.Controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uniTienda.Model.CarritoFolder.Carrito;
import com.uniTienda.Service.CarritoService;
import com.uniTienda.dto.ProductoCantidadRequest;

import jakarta.servlet.http.HttpServletRequest;
@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    // Endpoint para agregar productos al carrito
    @PostMapping("/agregarProductos")
public ResponseEntity<Carrito> agregarProductosAlCarrito(
        HttpServletRequest request,
        @RequestBody List<ProductoCantidadRequest> productos) {

    // Revisa si el usuario está autenticado
    boolean isAuthenticated = SecurityContextHolder.getContext().getAuthentication() != null &&
                              SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                              SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails;

    String sessionId = isAuthenticated ? null : request.getSession().getId();  // Usa sessionId solo si no está autenticado
    Carrito carrito = carritoService.agregarProductosAlCarrito(sessionId, productos);
    return ResponseEntity.ok(carrito);
}

    // Endpoint para obtener el carrito completo
    @GetMapping("/obtenerCarrito")
    public ResponseEntity<Carrito> obtenerCarrito(HttpServletRequest request) {
        // Obtiene el sessionId desde las cookies para usuarios no autenticados
        String sessionId = request.getSession().getId();

        Carrito carrito = carritoService.obtenerCarritoCompleto(sessionId);
        return ResponseEntity.ok(carrito);
    }

    // Endpoint para actualizar la cantidad de un producto en el carrito
    @PutMapping("/actualizarCantidad")
    public ResponseEntity<Carrito> actualizarCantidadProducto(
            HttpServletRequest request,
            @RequestParam Long productoId,
            @RequestParam int nuevaCantidad) {

        String sessionId = request.getSession().getId();
        Carrito carrito = carritoService.actualizarCantidadProducto(sessionId, productoId, nuevaCantidad);
        return ResponseEntity.ok(carrito);
    }

    // Endpoint para eliminar un producto del carrito
    @DeleteMapping("/eliminarProducto")
    public ResponseEntity<Void> eliminarProductoDelCarrito(
            HttpServletRequest request,
            @RequestParam Long productoId) {

        String sessionId = request.getSession().getId();
        carritoService.eliminarProductoDelCarrito(sessionId, productoId);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para obtener el total del carrito
    @GetMapping("/obtenerTotal")
    public ResponseEntity<BigDecimal> obtenerTotalCarrito(HttpServletRequest request) {
        String sessionId = request.getSession().getId();

        BigDecimal total = carritoService.obtenerTotalCarrito(sessionId);
        return ResponseEntity.ok(total);
    }
}