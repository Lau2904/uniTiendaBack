package com.uniTienda.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uniTienda.Model.Direccion;
import com.uniTienda.Model.Usuario;

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    List<Direccion> findByUsuario(Usuario usuario);
}