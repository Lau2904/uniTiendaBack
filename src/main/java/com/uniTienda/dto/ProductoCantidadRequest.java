package com.uniTienda.dto;

import lombok.Data;

@Data
public class ProductoCantidadRequest {

    private Long productoId;
    private int cantidad;

}
