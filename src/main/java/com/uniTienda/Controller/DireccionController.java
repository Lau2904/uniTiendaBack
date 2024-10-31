package com.uniTienda.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uniTienda.Model.Direccion;
import com.uniTienda.Service.DireccionService;
@RestController
@RequestMapping("/api/direcciones")
public class DireccionController {

    @Autowired
    private DireccionService direccionService;

    @PostMapping("/crearDireccion")
    public ResponseEntity<Direccion> crearDireccion(@RequestBody Direccion direccion) {
        Direccion nuevaDireccion = direccionService.agregarDireccion(direccion);
        return ResponseEntity.ok(nuevaDireccion);
    }

     // Obtener las direcciones de un usuario específico
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Direccion>> obtenerDireccionesPorUsuario(@PathVariable Long usuarioId) {
        List<Direccion> direcciones = direccionService.obtenerDireccionesPorUsuario(usuarioId);
        return ResponseEntity.ok(direcciones);
    }

    // Editar una dirección
    @PutMapping("/editarDireccion/{direccionId}")
    public ResponseEntity<Direccion> editarDireccion(
            @PathVariable Long direccionId,
            @RequestBody Direccion nuevaDireccion) {
        Direccion direccionActualizada = direccionService.editarDireccion(direccionId, nuevaDireccion);
        return ResponseEntity.ok(direccionActualizada);
    }

    // Eliminar una dirección
    @DeleteMapping("/eliminarDireccion/{direccionId}")
    public ResponseEntity<Void> eliminarDireccion(@PathVariable Long direccionId) {
        direccionService.eliminarDireccion(direccionId);
        return ResponseEntity.noContent().build();
    }
}