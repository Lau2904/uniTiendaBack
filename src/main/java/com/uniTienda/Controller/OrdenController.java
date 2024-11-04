package com.uniTienda.Controller;


import java.io.File;

import com.uniTienda.Model.Orden.Orden;
import com.uniTienda.Service.OrdenService;
import com.uniTienda.Service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;


import java.math.BigDecimal;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;



import com.uniTienda.dto.EstadoOrden;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    @Autowired
    private UsuarioService usuarioService;
// Crear una nueva orden desde el carrito
@PostMapping("/crearDesdeCarrito")
public ResponseEntity<Orden> crearOrdenDesdeCarrito(
        @RequestParam Long direccionId,
        @RequestParam(required = false) Long tarjetaId,
        @RequestParam String metodoPago,
        @RequestParam BigDecimal subtotal,
        Authentication authentication) {

    Long usuarioId = usuarioService.obtenerUsuarioAutenticadoId();
    Orden orden = ordenService.crearOrdenDesdeCarrito(usuarioId, direccionId, metodoPago, tarjetaId, subtotal);
    return ResponseEntity.ok(orden);
}

// Obtener detalles de una orden específica
@GetMapping("/{ordenId}")
public ResponseEntity<Orden> obtenerOrden(@PathVariable Long ordenId) {
    Long usuarioId = usuarioService.obtenerUsuarioAutenticadoId();
    Orden orden = ordenService.obtenerOrdenPorIdYUsuario(ordenId, usuarioId);
    return ResponseEntity.ok(orden);
}

// Actualizar el estado de una orden (admin o usuario autorizado)
 // Actualizar el estado de una orden (admin o usuario autorizado)
 @PutMapping("/{ordenId}/estado")
 public ResponseEntity<Orden> actualizarEstadoOrden(
         @PathVariable Long ordenId,
         @RequestParam String nuevoEstado) {

     Long usuarioId = usuarioService.obtenerUsuarioAutenticadoId();

     // Convertir el String a EstadoOrden
     EstadoOrden estado = EstadoOrden.valueOf(nuevoEstado.toUpperCase());

     Orden ordenActualizada = ordenService.actualizarEstadoOrden(ordenId, estado, usuarioId);
     return ResponseEntity.ok(ordenActualizada);
 }

// Listar todas las órdenes de un usuario
@GetMapping("/usuario")
public ResponseEntity<List<Orden>> obtenerOrdenesUsuario() {
    Long usuarioId = usuarioService.obtenerUsuarioAutenticadoId();
    List<Orden> ordenes = ordenService.obtenerOrdenesPorUsuario(usuarioId);
    return ResponseEntity.ok(ordenes);
}

// Admin: Obtener todas las órdenes (solo para administradores)
@GetMapping("/admin/todas")
public ResponseEntity<List<Orden>> obtenerTodasLasOrdenes() {
    List<Orden> ordenes = ordenService.obtenerTodasLasOrdenes();
    return ResponseEntity.ok(ordenes);
    }

    @PutMapping("/{ordenId}/cancelar")
    public ResponseEntity<Orden> cancelarOrden(@PathVariable Long ordenId) {
        Orden ordenCancelada = ordenService.cancelarOrden(ordenId);
        return ResponseEntity.ok(ordenCancelada);
    }

    @DeleteMapping("/{ordenId}/eliminar")
    public ResponseEntity<Void> eliminarOrden(@PathVariable Long ordenId) {
        ordenService.eliminarOrden(ordenId);
        return ResponseEntity.noContent().build(); // Responde con 204 No Content
    }

     @GetMapping("/{ordenId}/descargarFactura")
    public ResponseEntity<Resource> descargarFactura(@PathVariable Long ordenId) {
        // Buscar la orden en la base de datos
        Orden orden = ordenService.obtenerOrdenPorId(ordenId);
        if (orden == null || orden.getMetodoPago() == null || !orden.getMetodoPago().equalsIgnoreCase("GANA_GANA")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Construir la ruta al archivo PDF basado en el número de pedido
        String rutaFactura = "Factura_GanaGana_" + orden.getNumeroPedido() + ".pdf";
        Path path = Paths.get(rutaFactura);
        File archivoFactura = path.toFile();

        // Verificar si el archivo existe
        if (!archivoFactura.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Configurar los encabezados de la respuesta para la descarga
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + archivoFactura.getName());

        // Devolver el archivo como recurso descargable
        Resource recurso = new FileSystemResource(archivoFactura);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(archivoFactura.length())
                .body(recurso);
    }
}
