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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.uniTienda.Model.Usuario;
import com.uniTienda.Repository.UsuarioRepository;
import com.uniTienda.security.AuthCredentials;
import com.uniTienda.security.TokenUtils;

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
        Optional<Usuario> userOptional = usuarioRepository.findById(id.intValue());
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
        return usuarioRepository.findById(id.intValue()).orElseThrow(() -> new RuntimeException("User not found"));
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
    



}
