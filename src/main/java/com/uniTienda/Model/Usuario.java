package com.uniTienda.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Este campo se genera automáticamente

    private String nombre;
    private String apellido;
    private String email;
    private String telefono;

    @Column(name = "tipodocumento", nullable = false) // Nombre de la columna en la base de datos
    private String tipoDocumento;

    @Column(name = "numerodocumento", nullable = false) // Ajusta aquí el nombre según lo que haya en la base de datos
    private String numeroDocumento;

    private String password;

    @Column(name = "tipousuario") // Asegúrate de que esto coincida
    private String tipoUsuario; 

    @Transient // Esta anotación evita que el campo se persista en la base de datos
    private String googleToken;
    
}
