package com.uniTienda.dto.Productos;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductoRequest {
    private String nombre;
    private BigDecimal precio;
    private int stock;
    private String tamaño;
    private BigDecimal descuento;
    private List<String> colores;   // Lista de colores
    private List<String> imagenes;  // Cada imagen en formato Base64
    private String categoria;
}
