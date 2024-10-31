package com.uniTienda.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "direccion")
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String pais;
    private String departamento;
    private String ciudad;
    private String calle;
    private String barrio;
    private String codigoPostal;
    private String destinatario;
    
   

    @ManyToOne
    @JoinColumn(name = "cliente_id") 
    private Usuario usuario;

    // Getters y setters...
}
