package com.uniTienda.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.uniTienda.Model.CarritoFolder.Carrito;
import com.uniTienda.Model.CarritoFolder.CarritoDetalle;
import com.uniTienda.Model.Producto;
import com.uniTienda.Model.Usuario;
import com.uniTienda.Repository.CarritoFolder.CarritoDetalleRepository;
import com.uniTienda.Repository.CarritoFolder.CarritoRepository;
import com.uniTienda.Repository.ProductoRepository;
import com.uniTienda.Repository.UsuarioRepository;
import com.uniTienda.dto.ProductoCantidadRequest;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private CarritoDetalleRepository carritoDetalleRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;



    public Carrito agregarProductosAlCarrito(String sessionId, List<ProductoCantidadRequest> productos) {
        Carrito carrito = obtenerCarritoActivo(sessionId);

        for (ProductoCantidadRequest productoRequest : productos) {
            Long productoId = productoRequest.getProductoId();
            int cantidad = productoRequest.getCantidad();

            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            Optional<CarritoDetalle> detalleOpt = carritoDetalleRepository.findByCarritoAndProducto(carrito, producto);

            if (detalleOpt.isPresent()) {
                CarritoDetalle detalle = detalleOpt.get();
                detalle.setCantidad(detalle.getCantidad() + cantidad);
                detalle.setPrecioTotal(detalle.getProducto().getPrecio().multiply(BigDecimal.valueOf(detalle.getCantidad())));
                carritoDetalleRepository.save(detalle);
            } else {
                CarritoDetalle nuevoDetalle = new CarritoDetalle();
                nuevoDetalle.setCarrito(carrito);
                nuevoDetalle.setProducto(producto);
                nuevoDetalle.setCantidad(cantidad);
                nuevoDetalle.setPrecioTotal(producto.getPrecio().multiply(BigDecimal.valueOf(cantidad)));
                carritoDetalleRepository.save(nuevoDetalle);
            }
        }

        return carritoRepository.findById(carrito.getId()).orElse(carrito);
    }
    public Carrito actualizarCantidadProducto(String sessionId, Long productoId, int nuevaCantidad) {
        Carrito carrito = obtenerCarritoActivo(sessionId);
    
        // Obtén el objeto Producto usando productoId
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    
        CarritoDetalle detalle = carritoDetalleRepository.findByCarritoAndProducto(carrito, producto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en el carrito"));
    
        if (nuevaCantidad <= 0) {
            carritoDetalleRepository.delete(detalle);
        } else {
            detalle.setCantidad(nuevaCantidad);
            detalle.setPrecioTotal(detalle.getProducto().getPrecio().multiply(BigDecimal.valueOf(nuevaCantidad)));
            carritoDetalleRepository.save(detalle);
        }
    
        return carrito;
    }

    public void eliminarProductoDelCarrito(String sessionId, Long productoId) {
        Carrito carrito = obtenerCarritoActivo(sessionId);
    
        // Obtén el objeto Producto usando productoId
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    
        CarritoDetalle detalle = carritoDetalleRepository.findByCarritoAndProducto(carrito, producto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en el carrito"));
    
        carritoDetalleRepository.delete(detalle);
    }

    public Carrito obtenerCarritoCompleto(String sessionId) {
        return obtenerCarritoActivo(sessionId);
    }

    public BigDecimal obtenerTotalCarrito(String sessionId) {
        Carrito carrito = obtenerCarritoActivo(sessionId);

        return carrito.getDetalles().stream()
                .map(CarritoDetalle::getPrecioTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Carrito obtenerCarritoActivo(String sessionId) {
        if (isUserAuthenticated()) {
            Long usuarioId = obtenerUsuarioAutenticadoId();
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    
            return carritoRepository.findByClienteAndEstado(usuario, "ACTIVO")
                    .orElseGet(() -> crearNuevoCarritoParaUsuario(usuario));
        } else {
            return carritoRepository.findBySessionIdAndEstado(sessionId, "ACTIVO")
                    .orElseGet(() -> crearNuevoCarritoParaSesion(sessionId));
        }
    }

    private Carrito crearNuevoCarritoParaUsuario(Usuario usuario) {
        Carrito carrito = new Carrito();
        carrito.setCliente(usuario);  // Asigna el cliente
        carrito.setSessionId(null);   // Asegúrate de que session_id esté en NULL
        carrito.setEstado("ACTIVO");
        carrito.setFechaCreacion(LocalDateTime.now());
        return carritoRepository.save(carrito);
    }

    private Carrito crearNuevoCarritoParaSesion(String sessionId) {
        Carrito carrito = new Carrito();
        carrito.setCliente(null);      // Asegúrate de que cliente esté en NULL
        carrito.setSessionId(sessionId);  // Asigna el sessionId
        carrito.setEstado("ACTIVO");
        carrito.setFechaCreacion(LocalDateTime.now());
        return carritoRepository.save(carrito);
    }

    private boolean isUserAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() != null &&
               SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
               SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails;
    }

    private Long obtenerUsuarioAutenticadoId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        if(principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            String username = userDetails.getUsername();
            System.out.println("Usuario autenticado con email: " + username);
            
            Usuario usuario = usuarioRepository.findOneByEmail(username)
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
            
            return usuario.getId();
        } else {
            System.out.println("No se encontró un usuario autenticado en el contexto de seguridad.");
            throw new RuntimeException("No se pudo obtener el usuario autenticado");
        }
    }

    public void vaciarCarrito(Long usuarioId) {
        Optional<Carrito> carritoOptional = carritoRepository.findByClienteIdAndEstado(usuarioId, "ACTIVO");
        
        if (carritoOptional.isPresent()) {
            Carrito carrito = carritoOptional.get();
            carrito.getDetalles().clear();  // Limpiar los detalles del carrito
            carritoRepository.save(carrito);  // Guardar el carrito vacío
            System.out.println("Carrito vaciado exitosamente para el usuario con ID: " + usuarioId);
        } else {
            System.out.println("No se encontró un carrito activo para el usuario con ID: " + usuarioId);
            throw new RuntimeException("Carrito activo no encontrado para el usuario");
        }
    }
}
