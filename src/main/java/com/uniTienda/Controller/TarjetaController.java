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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uniTienda.Model.Orden.Tarjeta;
import com.uniTienda.Service.TarjetaService;
import com.uniTienda.dto.TarjetaRequest;
import com.uniTienda.dto.TipoTarjeta;

@RestController
@RequestMapping("/api/tarjeta")
public class TarjetaController {

    @Autowired
    private TarjetaService tarjetaService;

    // Endpoint para guardar una nueva tarjeta
    @PostMapping("/guardar")
    public ResponseEntity<String> guardarTarjeta(@RequestBody TarjetaRequest tarjetaRequest) {
        tarjetaService.guardarTarjeta(tarjetaRequest);
        return ResponseEntity.ok("Tarjeta guardada exitosamente");
    }

    // Endpoint para obtener todas las tarjetas del usuario autenticado
    @GetMapping("/obtener")
    public ResponseEntity<List<Tarjeta>> obtenerTarjetasUsuario() {
        List<Tarjeta> tarjetas = tarjetaService.obtenerTarjetasUsuario();
        return ResponseEntity.ok(tarjetas);
    }

    // Endpoint para actualizar una tarjeta existente
    @PutMapping("/actualizar/{tarjetaId}")
    public ResponseEntity<String> actualizarTarjeta(
            @PathVariable Long tarjetaId,
            @RequestBody TarjetaRequest tarjetaRequest) {
        tarjetaService.actualizarTarjeta(tarjetaId, tarjetaRequest);
        return ResponseEntity.ok("Tarjeta actualizada exitosamente");
    }
    

    // Endpoint para eliminar una tarjeta por ID
    @DeleteMapping("/eliminar/{tarjetaId}")
    public ResponseEntity<String> eliminarTarjeta(@PathVariable Long tarjetaId) {
        tarjetaService.eliminarTarjeta(tarjetaId);
        return ResponseEntity.ok("Tarjeta eliminada exitosamente");
    }

      @GetMapping("/obtenerPorTipo")
    public List<Tarjeta> obtenerTarjetasPorTipo(@RequestParam TipoTarjeta tipoTarjeta) {
        return tarjetaService.obtenerTarjetasPorTipo(tipoTarjeta);
    }
}