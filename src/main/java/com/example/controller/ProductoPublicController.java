package com.example.controller;

import com.example.dto.ProductoListDTO;
import com.example.model.Producto;
import com.example.repository.ProductoRepository;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import com.example.dto.ProductoDetailDTO;

@RestController
@RequestMapping("/api/public/productos")
public class ProductoPublicController {

    private final ProductoRepository productoRepository;

    public ProductoPublicController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping
    public Page<ProductoListDTO> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "recientes") String sort,
            @RequestParam(required = false) String categoria,   // 👈 nuevo
            @RequestParam(required = false) String q            // 👈 nuevo
    ) {
        Sort sortBy = switch (sort) {
            case "precio_asc"  -> Sort.by(Sort.Direction.ASC,  "precio");
            case "precio_desc" -> Sort.by(Sort.Direction.DESC, "precio");
            default            -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
        Pageable pageable = PageRequest.of(page, size, sortBy);

        boolean hasCategoria = categoria != null && !categoria.isBlank();
        boolean hasQ = q != null && !q.isBlank();

        Page<Producto> result;
        if (hasQ && hasCategoria) {
            result = productoRepository
                .findByNombreContainingIgnoreCaseAndCategoria_SlugIgnoreCase(q, categoria, pageable);
        } else if (hasQ) {
            result = productoRepository
                .findByNombreContainingIgnoreCase(q, pageable);
        } else if (hasCategoria) {
            result = productoRepository
                .findByCategoria_SlugIgnoreCase(categoria, pageable);
        } else {
            result = productoRepository.findAll(pageable);
        }

        return result.map(ProductoListDTO::new);
    }

    @GetMapping("/destacados")
    public Page<ProductoListDTO> getDestacados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Producto> result = productoRepository.findByDestacadoTrue(pageable);
        return result.map(ProductoListDTO::new);
    }

    @GetMapping("/{id}")
    public ProductoDetailDTO get(@PathVariable Long id) {
        Producto p = productoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));
        return new ProductoDetailDTO(p);
    }
}

