package com.uniTienda.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.uniTienda.Model.Usuario;
import com.uniTienda.Repository.UsuarioRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        Usuario usuario = usuarioRepository
        .findOneByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("El usuario con email " + email + " no existe" ));

        System.out.println("Usuario encontrado: " + usuario.getEmail());
        return new UserDetailsImpl(usuario);
    } 
}
