package com.uniTienda.Service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.uniTienda.Model.Direccion;
import com.uniTienda.Model.Usuario;
import com.uniTienda.Repository.DireccionRepository;
import com.uniTienda.Repository.UsuarioRepository;

@Service
public class DireccionService {

    private final DireccionRepository direccionRepository;
    private final UsuarioRepository usuarioRepository;

    public DireccionService(DireccionRepository direccionRepository, UsuarioRepository usuarioRepository) {
        this.direccionRepository = direccionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Direccion agregarDireccion(Direccion direccion) {
        String email = obtenerEmailUsuarioAutenticado();
        Usuario usuario = usuarioRepository.findOneByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Asocia la dirección al usuario autenticado
        direccion.setUsuario(usuario);
        return direccionRepository.save(direccion);
    }

    // Método para obtener el email del usuario autenticado
    private String obtenerEmailUsuarioAutenticado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    // Obtener direcciones de un usuario específico
    public List<Direccion> obtenerDireccionesPorUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return direccionRepository.findByUsuario(usuario);
    }

    // Editar una dirección existente
    public Direccion editarDireccion(Long direccionId, Direccion nuevaDireccion) {
        Direccion direccion = direccionRepository.findById(direccionId)
            .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));

        direccion.setPais(nuevaDireccion.getPais());
        direccion.setDepartamento(nuevaDireccion.getDepartamento());
        direccion.setCiudad(nuevaDireccion.getCiudad());
        direccion.setCodigoPostal(nuevaDireccion.getCodigoPostal());
        direccion.setCalle(nuevaDireccion.getCalle());
        direccion.setBarrio(nuevaDireccion.getBarrio());
        direccion.setDestinatario(nuevaDireccion.getDestinatario());

        return direccionRepository.save(direccion);
    }

    // Eliminar una dirección
    public void eliminarDireccion(Long direccionId) {
        if (!direccionRepository.existsById(direccionId)) {
            throw new RuntimeException("Dirección no encontrada");
        }
        direccionRepository.deleteById(direccionId);
    }


}
