package com.uniTienda.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import java.util.Date;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;


import com.itextpdf.text.Paragraph;

import com.itextpdf.text.pdf.PdfWriter;



public class FacturaPDFGenerator {
    
    public void generarFacturaPDF(String rutaArchivo, String codigoFactura, Date fechaEmision, String clienteNombre,
    String direccion, BigDecimal total) throws DocumentException {
try {
Document document = new Document();
PdfWriter.getInstance(document, new FileOutputStream(rutaArchivo));
document.open();

// Añadir contenido al PDF usando los parámetros recibidos
document.add(new Paragraph("Factura de Compra"));
document.add(new Paragraph("Número de Factura: " + codigoFactura));
document.add(new Paragraph("Fecha de Emisión: " + new SimpleDateFormat("dd/MM/yyyy").format(fechaEmision)));
document.add(new Paragraph("Cliente: " + clienteNombre));
document.add(new Paragraph("Dirección: " + direccion));
document.add(new Paragraph("Total: $" + total));

document.close();
} catch (FileNotFoundException e) {
throw new RuntimeException("Error al crear el archivo PDF", e);
}
    }
}