package com.uniTienda.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.itextpdf.text.DocumentException;
import com.uniTienda.Model.CarritoFolder.Carrito;
import com.uniTienda.Model.Direccion;
import com.uniTienda.Model.Orden.GanaGana;
import com.uniTienda.Model.Orden.Orden;
import com.uniTienda.Model.Orden.Tarjeta;
import com.uniTienda.Model.Pago;
import com.uniTienda.Model.Usuario;
import com.uniTienda.Repository.CarritoFolder.CarritoRepository;
import com.uniTienda.Repository.DireccionRepository;
import com.uniTienda.Repository.Orden.GanaGanaRepository;
import com.uniTienda.Repository.Orden.OrdenRepository;
import com.uniTienda.Repository.Orden.TarjetaRepository;
import com.uniTienda.Repository.PagoRepository;
import com.uniTienda.Repository.UsuarioRepository;
import com.uniTienda.dto.EstadoOrden;
import com.uniTienda.security.UserDetailsImpl;
import com.uniTienda.utils.FacturaPDFGenerator;

@Service
public class OrdenService {

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DireccionRepository direccionRepository;
    
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TarjetaRepository tarjetaRepository;

    @Autowired
    private GanaGanaRepository ganaGanaRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private PagoService pagoService;

    
   public Orden crearOrdenDesdeCarrito(Long usuarioId, Long direccionId, String metodoPago, Long tarjetaId, BigDecimal subtotal) {
    Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    Direccion direccion = direccionRepository.findById(direccionId)
            .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));

    Orden orden = new Orden();
    orden.setUsuario(usuario);
    orden.setDireccion(direccion);
    orden.setNumeroPedido(generarNumeroPedidoUnico()); // Generar número de pedido único
    orden.setMetodoPago(metodoPago);
    orden.setSubtotal(subtotal);

    // Calcular el costo de envío según el método de pago
    if ("RESERVA".equalsIgnoreCase(metodoPago)) {
        orden.setCostoEnvio(BigDecimal.ZERO); // Sin costo de envío para reservas
    } else {
        orden.setCostoEnvio(calcularCostoEnvio()); // Costo de envío normal para otros métodos de pago
    }

    orden.setTotal(orden.getSubtotal().add(orden.getCostoEnvio()));
    orden.setEstado(EstadoOrden.PENDIENTE);

    // Manejar métodos de pago y establecer fechas específicas
    if ("TARJETA".equalsIgnoreCase(metodoPago) && tarjetaId != null) {
        Tarjeta tarjeta = tarjetaRepository.findById(tarjetaId)
                .orElseThrow(() -> new RuntimeException("Tarjeta no encontrada"));
        orden.setTarjeta(tarjeta);
        orden.setFechaMaximaEntrega(calcularFechaEntrega(3));
    } else if ("GANA_GANA".equalsIgnoreCase(metodoPago)) {
        GanaGana factura = generarFacturaGanaGana(usuario, orden.getTotal());
        ganaGanaRepository.save(factura);
        orden.setGanaGana(factura);
        orden.setFechaMaximaEntrega(calcularFechaEntrega(3));

        // Generar el archivo PDF de la factura usando el método que tienes
        try {
            FacturaPDFGenerator facturaPDFGenerator = new FacturaPDFGenerator();
            String rutaFactura = "Factura_GanaGana_" + orden.getNumeroPedido() + ".pdf";
            facturaPDFGenerator.generarFacturaPDF(
                rutaFactura,
                factura.getCodigoFactura(),
                new Date(),
                usuario.getNombre() + " " + usuario.getApellido(),
                direccion.getCalle() + ", " + direccion.getCiudad() + ", " + direccion.getDepartamento() + ", " + direccion.getPais(),
                orden.getTotal()
            );
            System.out.println("Factura PDF generada en: " + rutaFactura);
        } catch (DocumentException | io.jsonwebtoken.io.IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar la factura PDF", e);
        }

    } else if ("RESERVA".equalsIgnoreCase(metodoPago)) {
        orden.setFechaMaximaReclamo(calcularFechaEntrega(3));
    } else {
        throw new IllegalArgumentException("Debe proporcionar un método de pago válido.");
    }

    // Guardar la orden y crear un registro de pago
    orden = ordenRepository.save(orden);

    // Crear registro de pago asociado a la orden
    Pago pago = new Pago();
    pago.setOrden(orden);
    pago.setMonto(orden.getTotal());
    pago.setFecha(LocalDateTime.now());
    pago.setMetodoPago(metodoPago);
    pago.setEstado("PENDIENTE"); // Inicializa como pendiente
    pagoRepository.save(pago);

    // Enviar correo de confirmación
    usuarioService.enviarCorreoConfirmacionOrden(orden);

    // Limpiar el carrito del usuario después de crear la orden
    carritoService.vaciarCarrito(usuarioId);

    return orden;
}
public Orden obtenerOrdenPorId(Long ordenId) {
    return ordenRepository.findById(ordenId)
            .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
}
    // Método auxiliar para calcular la fecha de entrega o reclamo a partir de la cantidad de días
    private Date calcularFechaEntrega(int dias) {
        LocalDate fechaEntrega = LocalDate.now().plusDays(dias);
        return Date.from(fechaEntrega.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    // Método para generar número de pedido único de 10 caracteres
    private String generarNumeroPedidoUnico() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }
// Método para generar número de pedido único de 10 caracteres

    // Método auxiliar para calcular el subtotal desde el carrito
    private BigDecimal calcularSubtotalDesdeCarrito(Long usuarioId) {
        Carrito carrito = carritoRepository.findByClienteIdAndEstado(usuarioId, "ACTIVO")
                .orElseThrow(() -> new RuntimeException("Carrito activo no encontrado para el usuario"));
        return carrito.getDetalles().stream()
                .map(detalle -> detalle.getPrecioTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Método para generar un número de pedido alfanumérico de 10 caracteres
    
// Método para generar una factura Gana Gana
private GanaGana generarFacturaGanaGana(Usuario usuario, BigDecimal montoTotal) {
    GanaGana factura = new GanaGana();
    factura.setCliente(usuario);
    factura.setCodigoFactura(UUID.randomUUID().toString());
    factura.setFechaLimitePago(convertToDateViaInstant(LocalDate.now().plusDays(3))); // Fecha límite de 3 días
    factura.setMonto(montoTotal);
    factura.setEstado("PENDIENTE");
    return factura;
}
    // Obtener una orden específica por ID y usuario
    public Orden obtenerOrdenPorIdYUsuario(Long ordenId, Long usuarioId) {
        return ordenRepository.findById(ordenId)
            .filter(orden -> orden.getUsuario().getId().equals(usuarioId))
            .orElseThrow(() -> new RuntimeException("Orden no encontrada o no pertenece al usuario"));
    }

    // Obtener todas las órdenes de un usuario
    public List<Orden> obtenerOrdenesPorUsuario(Long usuarioId) {
        return ordenRepository.findByUsuarioId(usuarioId);
    }

    // Obtener todas las órdenes (para administradores)
    public List<Orden> obtenerTodasLasOrdenes() {
        return ordenRepository.findAll();
    }

    // Actualizar el estado de una orden
    public Orden actualizarEstadoOrden(Long ordenId, EstadoOrden nuevoEstado, Long usuarioId) {
        Orden orden = obtenerOrdenPorIdYUsuario(ordenId, usuarioId);
        orden.setEstado(nuevoEstado);
        ordenRepository.save(orden);
    
        // Verifica si existe un pago asociado a la orden
        Optional<Pago> pagoOptional = pagoRepository.findByOrdenId(ordenId).stream().findFirst();
        if (pagoOptional.isPresent()) {
            Pago pago = pagoOptional.get();
    
            // Si la orden es cancelada, actualiza el estado del pago a "CANCELADO"
            if (nuevoEstado == EstadoOrden.CANCELADO) {
                pago.setEstado("CANCELADO");
                pagoRepository.save(pago);
            } else if ((orden.getMetodoPago().equals("TARJETA") || orden.getMetodoPago().equals("GANA_GANA"))
                    && nuevoEstado == EstadoOrden.PENDIENTE) {
                pago.setEstado("COMPLETADO");
                pagoRepository.save(pago);
    
                // Enviar notificación solo si el método de pago es TARJETA o GANA_GANA y el estado es CONFIRMADO
                pagoService.enviarCorreoNotificacionAdministrador(pago);
            } else if (orden.getMetodoPago().equals("RESERVA") && nuevoEstado == EstadoOrden.ENTREGADO) {
                pago.setEstado("COMPLETADO");
                pagoRepository.save(pago);
            }
        }
    
        return orden;
    }
    // Método para confirmar una orden y actualizar la factura de GanaGana si existe
    public Orden confirmarOrden(Long ordenId) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        orden.setEstado(EstadoOrden.CONFIRMADO);
        orden = ordenRepository.save(orden);

        // Si la orden tiene una factura de GanaGana, actualiza su estado a "PAGADO" y ajusta el monto
        if (orden.getGanaGana() != null) {
            GanaGana factura = orden.getGanaGana();
            factura.setEstado("PAGADO");
            factura.setMonto(orden.getTotal());
            ganaGanaRepository.save(factura);
        }

        return orden;
    }

    public Orden cancelarOrden(Long ordenId) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        // Cambia el estado a CANCELADO
        orden.setEstado(EstadoOrden.CANCELADO);
        return ordenRepository.save(orden);
    }

    public void eliminarOrden(Long ordenId) {
        if (!ordenRepository.existsById(ordenId)) {
            throw new RuntimeException("Orden no encontrada");
        }
        ordenRepository.deleteById(ordenId);
    }

    
    private BigDecimal calcularCostoEnvio() {
        return new BigDecimal("3100"); // Ejemplo de costo fijo de envío
    }

    // Obtener el usuario autenticado
    private Usuario obtenerUsuarioAutenticado() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return usuarioRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
    }

    // Convertir LocalDate a Date
    private Date convertToDateViaInstant(LocalDate dateToConvert) {
        return Date.from(dateToConvert.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    

    
}
