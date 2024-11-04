package com.uniTienda.dto;

import lombok.Data;

@Data
public class TarjetaRequest {
    private String numeroTarjeta;
    private String tipoTarjeta; // "CREDITO" o "DEBITO"
    private String nombreTitular;
    private String fechaExpiracion; // formato "MM/yyyy"

}