package com.uniTienda.Service;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.uniTienda.Model.Productos.Producto;
import com.uniTienda.Repository.Productos.ProductoRepository;
import com.uniTienda.dto.Productos.ProductoRequest;



@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    // Crear un producto con imagen
    public Producto crearProducto(ProductoRequest productoRequest) {
        Producto producto = new Producto();
        producto.setNombre(productoRequest.getNombre());
        producto.setPrecio(productoRequest.getPrecio());
        producto.setStock(productoRequest.getStock());
        producto.setTamaño(productoRequest.getTamaño());
        producto.setDescuento(productoRequest.getDescuento());
        producto.setColores(productoRequest.getColores());
    
        // Guardar todas las rutas de imágenes en la entidad
        producto.setImagenes(productoRequest.getImagenes());
        producto.setCategoria(productoRequest.getCategoria());
    
        return productoRepository.save(producto);
    }

    // Buscar producto por ID
    public Optional<Producto> buscarProductoPorId(Long id) {
        return productoRepository.findById(id);
    }

    // Seleccionar todos los productos
    public List<Producto> obtenerTodosLosProductos() {
        return productoRepository.findAll();
    }

    // Buscar productos por nombre
    public List<Producto> buscarProductosPorNombre(String nombre) {
        return productoRepository.findByNombreContaining(nombre);
    }

    // Eliminar producto por ID
    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }

    // Actualizar producto
    public Optional<Producto> actualizarProducto(Long id, ProductoRequest productoRequest) {
        return productoRepository.findById(id).map(producto -> {
            producto.setNombre(productoRequest.getNombre());
            producto.setPrecio(productoRequest.getPrecio());
            producto.setStock(productoRequest.getStock());
            producto.setTamaño(productoRequest.getTamaño());
            producto.setDescuento(productoRequest.getDescuento());
            producto.setColores(productoRequest.getColores());
            producto.setImagenes(productoRequest.getImagenes());
            producto.setCategoria(productoRequest.getCategoria());
            return productoRepository.save(producto);
        });
    }
     // Filtrar productos por color
     // Método para buscar productos que contengan al menos uno de los colores especificados
    public List<Producto> filtrarPorColores(List<String> colores) {
        // Convierte la lista de colores a un array de Strings para la consulta
        String[] coloresArray = colores.toArray(new String[0]);
        return productoRepository.findByColoresIn(coloresArray);
    }

    // Obtener todos los productos ordenados por precio de menor a mayor
    public List<Producto> ordenarPorPrecioAsc() {
        return productoRepository.findAll(Sort.by(Sort.Direction.ASC, "precio"));
    }

    // Obtener todos los productos ordenados por precio de mayor a menor
    public List<Producto> ordenarPorPrecioDesc() {
        return productoRepository.findAll(Sort.by(Sort.Direction.DESC, "precio"));
    }

    // Obtener todos los productos ordenados alfabéticamente por nombre (A-Z)
    public List<Producto> ordenarPorNombreAsc() {
        return productoRepository.findAll(Sort.by(Sort.Direction.ASC, "nombre"));
    }
     // Método para obtener productos dentro de un rango de precios
    public List<Producto> filtrarPorRangoDePrecios(BigDecimal precioMin, BigDecimal precioMax) {
        return productoRepository.findByPrecioBetween(precioMin, precioMax);
    }

    // Método para obtener productos con descuento disponible
    public List<Producto> filtrarConDescuento() {
        return productoRepository.findWithDescuento();

    }

}
