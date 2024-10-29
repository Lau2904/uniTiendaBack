package com.uniTienda.Controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uniTienda.Model.Usuario;
import com.uniTienda.Service.UsuarioService;
import com.uniTienda.dto.ResponseMessage;
import com.uniTienda.security.AuthCredentials;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@AllArgsConstructor
public class UsuarioController {

    private final UsuarioService usuService;

    @PostMapping("/registro")
    public ResponseEntity<Usuario> registerUser(@RequestBody Usuario usuario) {
        Usuario newUser = usuService.registerUser(usuario);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody AuthCredentials authCredentials) {
        String token = usuService.loginUser(authCredentials);
        return ResponseEntity.ok(token);
    }



    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUser(@PathVariable Long id, @RequestBody Usuario updatedInfo) {
        Usuario updatedUser = usuService.updateUserInfo(id, updatedInfo);
        return ResponseEntity.ok(updatedUser);
    }

    // Solo ADMIN puede acceder
   
   
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUserById(@PathVariable Long id) {
        Usuario user = usuService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Usuario>> getAllUsers() {
        List<Usuario> users = usuService.getAllUsers();
        return ResponseEntity.ok(users);
    }
     @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        // Crear un mensaje en el cuerpo de la respuesta para confirmar el logout
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout exitoso");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/sendResetPasswordCode")
    public ResponseEntity<ResponseMessage> sendResetPasswordCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMessage("El parámetro 'email' es requerido."));
        }

        boolean success = usuService.sendResetPasswordCode(email);
        if (success) {
            return ResponseEntity.ok(new ResponseMessage("Código enviado con éxito."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Usuario no encontrado."));
        }
    }

    @PostMapping("/verifyResetPasswordCode")
    public ResponseEntity<Map<String, String>> verifyResetPasswordCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        if (email == null || code == null || email.isEmpty() || code.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Faltan los parámetros requeridos."));
        }

        boolean valid = usuService.verifyResetPasswordCode(email, code);
        if (valid) {
            return ResponseEntity.ok(Map.of("message", "Código verificado correctamente."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Código incorrecto o expirado."));
        }
    }

    @PutMapping("/resetPassword")
public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
    String email = request.get("email");
    String newPassword = request.get("newPassword");

    if (email == null || newPassword == null || email.isEmpty() || newPassword.isEmpty()) {
        return ResponseEntity.badRequest().body(Map.of("message", "Email y nueva contraseña son requeridos."));
    }

    boolean success = usuService.resetPassword(email, newPassword);
    if (success) {
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada exitosamente."));
    } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "No se pudo actualizar la contraseña. Código de verificación inválido o expirado."));
    }
}

}
