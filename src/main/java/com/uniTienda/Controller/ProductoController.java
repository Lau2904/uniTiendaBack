package com.uniTienda.Controller;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.uniTienda.Model.Producto;
import com.uniTienda.Service.ProductoService;
import com.uniTienda.dto.ProductoRequest;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private static final String IMAGE_DIRECTORY = "C:/Users/Laura Sofia/Downloads/imagenes/";

    @Autowired
    private ProductoService productoService;

    // Crear un producto con imagen
    @PostMapping
    public ResponseEntity<Producto> crearProducto(
            @RequestPart("producto") ProductoRequest productoRequest,
            @RequestPart("imagenes") List<MultipartFile> imagenes) {
        try {
            // Lista para almacenar las rutas de todas las imágenes
            List<String> rutasImagenes = new ArrayList<>();

            // Guardar cada imagen y agregar su ruta a la lista
            for (MultipartFile imagen : imagenes) {
                String nombreArchivo = productoRequest.getNombre() + "_" + imagen.getOriginalFilename();
                Path rutaArchivo = Paths.get(IMAGE_DIRECTORY, nombreArchivo);
                Files.write(rutaArchivo, imagen.getBytes());
                
                // Añadir la ruta relativa o absoluta de la imagen a la lista
                rutasImagenes.add(IMAGE_DIRECTORY + nombreArchivo);
            }

            // Asignar todas las rutas de imágenes al request del producto
            productoRequest.setImagenes(rutasImagenes);

            // Crear y guardar el producto con las imágenes
            Producto producto = productoService.crearProducto(productoRequest);

            return new ResponseEntity<>(producto, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Buscar producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscarProductoPorId(@PathVariable Long id) {
        Optional<Producto> producto = productoService.buscarProductoPorId(id);
        return producto.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Seleccionar todos los productos
    @GetMapping
    public ResponseEntity<List<Producto>> obtenerTodosLosProductos() {
        List<Producto> productos = productoService.obtenerTodosLosProductos();
        return ResponseEntity.ok(productos);
    }

    // Buscar productos por nombre
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarProductosPorNombre(@RequestParam String nombre) {
        List<Producto> productos = productoService.buscarProductosPorNombre(nombre);
        return ResponseEntity.ok(productos);
    }

    // Eliminar producto por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    // Actualizar producto
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(
            @PathVariable Long id,
            @RequestBody ProductoRequest productoRequest) {
        Optional<Producto> productoActualizado = productoService.actualizarProducto(id, productoRequest);
        return productoActualizado.map(ResponseEntity::ok)
                                  .orElseGet(() -> ResponseEntity.notFound().build());
    }
    // Filtrar productos por color
    @GetMapping("/filtrarPorColores")
    public ResponseEntity<List<Producto>> filtrarPorColores(@RequestParam List<String> colores) {
        List<Producto> productos = productoService.filtrarPorColores(colores);
        return ResponseEntity.ok(productos);
    }

    // Ordenar productos por precio de menor a mayor
    @GetMapping("/ordenarPorPrecioAsc")
    public ResponseEntity<List<Producto>> ordenarPorPrecioAsc() {
        List<Producto> productos = productoService.ordenarPorPrecioAsc();
        return ResponseEntity.ok(productos);
    }

    // Ordenar productos por precio de mayor a menor
    @GetMapping("/ordenarPorPrecioDesc")
    public ResponseEntity<List<Producto>> ordenarPorPrecioDesc() {
        List<Producto> productos = productoService.ordenarPorPrecioDesc();
        return ResponseEntity.ok(productos);
    }

    // Ordenar productos alfabéticamente (A-Z)
    @GetMapping("/ordenarPorNombreAsc")
    public ResponseEntity<List<Producto>> ordenarPorNombreAsc() {
        List<Producto> productos = productoService.ordenarPorNombreAsc();
        return ResponseEntity.ok(productos);
    }

    // Endpoint para filtrar productos dentro de un rango de precios
    @GetMapping("/filtrarPorRangoDePrecios")
    public ResponseEntity<List<Producto>> filtrarPorRangoDePrecios(
            @RequestParam BigDecimal precioMin,
            @RequestParam BigDecimal precioMax) {
        List<Producto> productos = productoService.filtrarPorRangoDePrecios(precioMin, precioMax);
        return ResponseEntity.ok(productos);
    }

    // Endpoint para obtener productos con descuento disponible
    @GetMapping("/conDescuento")
    public ResponseEntity<List<Producto>> filtrarConDescuento() {
        List<Producto> productos = productoService.filtrarConDescuento();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/categoria")
    public ResponseEntity<List<Producto>> getProductosPorCategoria(@RequestParam String categoria) {
        List<Producto> productos = productoService.getProductosPorCategoria(categoria);
        return ResponseEntity.ok(productos);
    }



    
}