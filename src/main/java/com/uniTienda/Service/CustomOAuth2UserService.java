package com.uniTienda.Service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.uniTienda.Model.Usuario;
import com.uniTienda.Repository.UsuarioRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        // Extrae la información de usuario desde el perfil de Google
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        // Verifica si el usuario ya existe en la base de datos
        Usuario usuario = usuarioRepository.findOneByEmail(email).orElse(null);
        if (usuario == null) {
            // Si no existe, crea y guarda un nuevo usuario
            usuario = new Usuario();
            usuario.setEmail(email);
            usuario.setNombre(name);
            usuarioRepository.save(usuario);
        }

        // Devuelve un usuario autenticado para continuar con el flujo de Spring Security
        return new DefaultOAuth2User(oAuth2User.getAuthorities(), attributes, "name");
    }
}