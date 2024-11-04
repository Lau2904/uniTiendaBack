package com.uniTienda.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.uniTienda.Model.Orden.Tarjeta;
import com.uniTienda.Model.Usuario;
import com.uniTienda.Repository.Orden.TarjetaRepository;
import com.uniTienda.Repository.UsuarioRepository;
import com.uniTienda.dto.TarjetaRequest;
import com.uniTienda.dto.TipoTarjeta;
import com.uniTienda.security.UserDetailsImpl;

@Service
public class TarjetaService {

    @Autowired
    private TarjetaRepository tarjetaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public void guardarTarjeta(TarjetaRequest tarjetaRequest) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        YearMonth yearMonth = YearMonth.parse(tarjetaRequest.getFechaExpiracion(), formatter);

        // Crear una instancia de Tarjeta con los datos de TarjetaRequest
        Tarjeta tarjetaInfo = new Tarjeta();
        tarjetaInfo.setNumeroTarjeta(tarjetaRequest.getNumeroTarjeta()); // Asignar número de tarjeta (últimos 4 dígitos)
        tarjetaInfo.setTipoTarjeta(TipoTarjeta.valueOf(tarjetaRequest.getTipoTarjeta())); // Asegurar que el tipo es correcto
        tarjetaInfo.setNombreTitular(tarjetaRequest.getNombreTitular()); // Asignar el nombre del titular
        tarjetaInfo.setFechaExpiracion(yearMonth); // Asignar la fecha de expiración como YearMonth

        // Obtener el Usuario autenticado y asignarlo a la tarjeta
        Usuario usuario = obtenerUsuarioAutenticado();
        tarjetaInfo.setUsuario(usuario);

        // Guardar la tarjeta en el repositorio
        tarjetaRepository.save(tarjetaInfo);
    }

    public List<Tarjeta> obtenerTarjetasUsuario() {
        Usuario usuario = obtenerUsuarioAutenticado();
        return tarjetaRepository.findByUsuario(usuario);
    }

    private Usuario obtenerUsuarioAutenticado() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return usuarioRepository.findById(userDetails.getId())
            .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
    }

    public Tarjeta actualizarTarjeta(Long tarjetaId, TarjetaRequest tarjetaRequest) {
        Tarjeta tarjetaExistente = tarjetaRepository.findById(tarjetaId)
                .orElseThrow(() -> new RuntimeException("Tarjeta no encontrada con ID: " + tarjetaId));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        YearMonth yearMonth = YearMonth.parse(tarjetaRequest.getFechaExpiracion(), formatter);

        tarjetaExistente.setNombreTitular(tarjetaRequest.getNombreTitular());
        tarjetaExistente.setFechaExpiracion(yearMonth);
        tarjetaExistente.setTipoTarjeta(TipoTarjeta.valueOf(tarjetaRequest.getTipoTarjeta()));
        tarjetaExistente.setNumeroTarjeta(tarjetaRequest.getNumeroTarjeta());

        return tarjetaRepository.save(tarjetaExistente);
    }

    public void eliminarTarjeta(Long tarjetaId) {
        tarjetaRepository.deleteById(tarjetaId);
    }
    public List<Tarjeta> obtenerTarjetasPorTipo(TipoTarjeta tipoTarjeta) {
        Usuario usuario = obtenerUsuarioAutenticado();
        return tarjetaRepository.findByUsuarioAndTipoTarjeta(usuario, tipoTarjeta);
    }

    public Tarjeta obtenerTarjetaPorId(Long tarjetaId) {
        return tarjetaRepository.findById(tarjetaId)
                .orElseThrow(() -> new RuntimeException("Tarjeta no encontrada con ID: " + tarjetaId));
    }


}
