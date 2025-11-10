package com.example.service;

import com.example.model.Producto;
import com.example.repository.ProductoRepository;
import com.example.repository.DetalleFacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private DetalleFacturaRepository detalleFacturaRepository;

    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> getProductoById(Long id) {
        return productoRepository.findById(id);
    }

    public Producto createProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto updateProducto(Long id, Producto productoDetails) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        producto.setNombre(productoDetails.getNombre());
        producto.setDescripcion(productoDetails.getDescripcion());
        producto.setPrecio(productoDetails.getPrecio());
        producto.setStock(productoDetails.getStock());
        producto.setImagenUrl(productoDetails.getImagenUrl());
        producto.setCategoria(productoDetails.getCategoria());
        producto.setProveedor(productoDetails.getProveedor());
        producto.setDestacado(productoDetails.getDestacado());
        producto.setActivo(productoDetails.getActivo());
        return productoRepository.save(producto);
    }

    public void deleteProducto(Long id) {
        // Check if the product exists
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
        
        // Check if the product is being used in any factura detalle
        long facturaCount = detalleFacturaRepository.countByProducto_Id(id);
        if (facturaCount > 0) {
            throw new IllegalStateException("No se puede eliminar el producto porque está incluido en " + facturaCount + " factura(s).");
        }
        
        productoRepository.deleteById(id);
    }
}
