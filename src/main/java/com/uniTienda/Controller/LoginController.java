package com.uniTienda.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.uniTienda.Model.Usuario;
import com.uniTienda.Repository.UsuarioRepository;

@Controller
public class LoginController {

     @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/oauth2/success")
    public String loginSuccess(@AuthenticationPrincipal OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        
        // Verificar si el usuario ya tiene sus datos completos
        Usuario usuario = usuarioRepository.findOneByEmail(email).orElse(null);
        if (usuario != null && (usuario.getTelefono() == null || usuario.getTipoDocumento() == null || usuario.getNumeroDocumento() == null)) {
            return "redirect:/complete-profile"; // Redirige a completar el perfil
        }
        
        return "redirect:/home"; // Redirige a la página principal si el perfil está completo
    }
}