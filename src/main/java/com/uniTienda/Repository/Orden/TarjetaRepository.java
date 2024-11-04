package com.uniTienda.Repository.Orden;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uniTienda.Model.Orden.Tarjeta;
import com.uniTienda.Model.Usuario;
import com.uniTienda.dto.TipoTarjeta;

public interface TarjetaRepository extends JpaRepository<Tarjeta, Long> {
     List<Tarjeta> findByUsuario(Usuario usuario);  
      List<Tarjeta> findByUsuarioAndTipoTarjeta(Usuario usuario, TipoTarjeta tipoTarjeta);
}
