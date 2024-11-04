package com.uniTienda.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uniTienda.Model.Usuario;


public interface  UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findOneByEmail(String email);
    List<Usuario> findByTipoUsuario(String tipoUsuario);
}
