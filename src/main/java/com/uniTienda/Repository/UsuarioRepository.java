package com.uniTienda.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uniTienda.Model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByEmail(String email);
}