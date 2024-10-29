package com.uniTienda.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uniTienda.Model.Usuario;


public interface  UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    Optional<Usuario> findOneByEmail(String email);
}
