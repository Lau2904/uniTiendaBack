package com.uniTienda.Service;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.uniTienda.Model.Orden.Orden;
import com.uniTienda.Model.Pago;
import com.uniTienda.Repository.Orden.OrdenRepository;
import com.uniTienda.Repository.PagoRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private OrdenRepository ordenRepository;
    private JavaMailSender mailSender;

    public PagoService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Método para crear un nuevo pago
    public Pago crearPago(Long ordenId, BigDecimal monto, String metodoPago) {
    Orden orden = ordenRepository.findById(ordenId)
            .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

    Pago pago = new Pago();
    pago.setMonto(monto);
    
    // Convertir Date a LocalDateTime
    LocalDateTime fechaActual = LocalDateTime.now(ZoneId.systemDefault());
    pago.setFecha(fechaActual);
    
    pago.setMetodoPago(metodoPago);
    pago.setEstado("PENDIENTE"); // Estado inicial del pago
    pago.setOrden(orden);

    return pagoRepository.save(pago);
}

    // Método para actualizar el estado de un pago
    public Pago actualizarEstadoPago(Long pagoId, String nuevoEstado) {
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        pago.setEstado(nuevoEstado);
        return pagoRepository.save(pago);
    }

    // Método para obtener todos los pagos de una orden específica
    public List<Pago> obtenerPagosPorOrden(Long ordenId) {
        return pagoRepository.findByOrdenId(ordenId);
    }

    // Método para obtener todos los pagos por estado
    public List<Pago> obtenerPagosPorEstado(String estado) {
        return pagoRepository.findByEstado(estado);
    }

    // Método para obtener todos los pagos por método de pago
    public List<Pago> obtenerPagosPorMetodo(String metodoPago) {
        return pagoRepository.findByMetodoPago(metodoPago);
    }

    public void enviarCorreoNotificacionAdministrador(Pago pago) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);
            
            String destinatario = "laurasofiarojas0429@gmail.com"; // Correo del administrador
            helper.setTo(destinatario);
            helper.setSubject("Notificación de Pago Completado");
            
            String contenido = "<h1>Pago Completado para la Orden #" + pago.getOrden().getNumeroPedido() + "</h1>"
                    + "<p>Detalles del pago:</p>"
                    + "<ul>"
                    + "<li><strong>ID del Pago:</strong> " + pago.getId() + "</li>"
                    + "<li><strong>Monto:</strong> $" + pago.getMonto() + "</li>"
                    + "<li><strong>Método de Pago:</strong> " + pago.getMetodoPago() + "</li>"
                    + "<li><strong>Estado:</strong> " + pago.getEstado() + "</li>"
                    + "</ul>"
                    + "<p>Este pago ha sido confirmado y está listo para procesar.</p>";
            
            helper.setText(contenido, true);
            mailSender.send(mensaje);
            System.out.println("Notificación enviada al administrador: " + destinatario);
            
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al enviar la notificación al administrador", e);
        }
    }
}
