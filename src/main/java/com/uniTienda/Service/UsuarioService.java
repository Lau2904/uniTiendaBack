package com.uniTienda.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.uniTienda.Model.Direccion;
import com.uniTienda.Model.Orden.Orden;
import com.uniTienda.Model.Usuario;
import com.uniTienda.Repository.UsuarioRepository;
import com.uniTienda.security.AuthCredentials;
import com.uniTienda.security.TokenUtils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JavaMailSender mailSender;

    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();

    public Usuario registerUser(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
    
        // Verifica una característica, en este caso si el email contiene "@empresa.com" para asignar rol de ADMIN
        if (usuario.getEmail().endsWith("@empresa.com")) {
            usuario.setTipoUsuario("Admin");
        } else {
            usuario.setTipoUsuario("Cliente");
        }
    
        return usuarioRepository.save(usuario);
    }

   
    
    public String loginUser(AuthCredentials authCredentials) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authCredentials.getEmail(),
                authCredentials.getPassword()
            )
        );
        
        Usuario usuario = usuarioRepository.findOneByEmail(authCredentials.getEmail()).orElseThrow();
        return TokenUtils.createToken(usuario.getNombre(), usuario.getEmail());
    }


    public Usuario updateUserInfo(Long id, Usuario updatedInfo) {
        Optional<Usuario> userOptional = usuarioRepository.findById(id);
        if (userOptional.isPresent()) {
            Usuario user = userOptional.get();
            user.setNombre(updatedInfo.getNombre());
            user.setApellido(updatedInfo.getApellido());
            user.setTelefono(updatedInfo.getTelefono());
            user.setTipoDocumento(updatedInfo.getTipoDocumento());
            user.setNumeroDocumento(updatedInfo.getNumeroDocumento());
            return usuarioRepository.save(user);
        }
        throw new RuntimeException("User not found with id: " + id);
    }

    public Usuario getUserById(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<Usuario> getAllUsers() {
        return usuarioRepository.findAll();
    }

      public boolean sendResetPasswordCode(String email) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findOneByEmail(email);
        if (usuarioOptional.isPresent()) {
            String code = String.format("%06d", new Random().nextInt(999999));
            verificationCodes.put(email, code);
            expireCodeAfter(email, 3, TimeUnit.MINUTES);
            sendEmailWithCode(email, code);
            return true;
        }
        return false;
    }

    public void sendEmailWithCode(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("Código de verificación");
            helper.setText("Su código de verificación es: " + code);
            mailSender.send(message);
        } catch (Exception e) {
          
        }
    }

    public synchronized boolean verifyResetPasswordCode(String email, String code) {
        String storedCode = verificationCodes.get(email);
        if (storedCode != null && storedCode.equals(code)) {
            verificationCodes.remove(email);
            return true;
        }
        return false;
    }

    private void expireCodeAfter(String email, long timeout, TimeUnit unit) {
        new Thread(() -> {
            try {
                unit.sleep(timeout);
                verificationCodes.remove(email);
            } catch (InterruptedException e) {
                
            }
        }).start();
    }

   
    // Método para cambiar la contraseña sin requerir el código
    public boolean resetPassword(String email, String newPassword) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findOneByEmail(email);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            usuario.setPassword(passwordEncoder.encode(newPassword));
            usuarioRepository.save(usuario);
            return true;
        }
        return false;
    }

     public Long obtenerUsuarioAutenticadoId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        Usuario usuario = usuarioRepository.findOneByEmail(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return usuario.getId();
    }

        public void enviarCorreoConfirmacionOrden(Orden orden) {
        String destinatario = orden.getUsuario().getEmail();
        String asunto = "Creación de Pedido #" + orden.getNumeroPedido();
        String mensaje = construirMensajeConfirmacion(orden);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(mensaje, true); // true para interpretar como HTML

            mailSender.send(mimeMessage);
            System.out.println("Correo de confirmación enviado a: " + destinatario);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al enviar el correo de confirmación", e);
        }
    }

    private String construirMensajeConfirmacion(Orden orden) {
        String direccionFormateada = formatearDireccion(orden.getDireccion());

        return "<h1>Creación de Pedido #" + orden.getNumeroPedido() + "</h1>"
                + "<p>Estimado/a " + orden.getUsuario().getNombre() + ",</p>"
                + "<p>Gracias por tu compra. Aquí tienes los detalles de tu pedido:</p>"
                + "<ul>"
                + "<li><strong>Dirección de entrega:</strong> " + direccionFormateada + "</li>"
                + "<li><strong>Método de pago:</strong> " + orden.getMetodoPago() + "</li>"
                + "<li><strong>Total:</strong> $" + orden.getTotal() + "</li>"
                + "<li><strong>Fecha de entrega:</strong> " + (orden.getFechaMaximaEntrega() != null ? orden.getFechaMaximaEntrega() : "N/A") + "</li>"
                + "</ul>"
                + "<p>Te mantendremos informado/a sobre el estado de tu pedido a traves de de pestaña pedidos</p>"
                + "<p>Saludos,</p>"
                + "<p><strong>Equipo de uniTienda</strong></p>";
    }

    private String formatearDireccion(Direccion direccion) {
        return String.format(
            "%s, %s, %s, %s, %s",
            direccion.getCalle(),
            direccion.getCiudad(),
            direccion.getDepartamento(),
            direccion.getPais(),
            direccion.getCodigoPostal() != null ? direccion.getCodigoPostal() : ""
        );
    }

    



}
