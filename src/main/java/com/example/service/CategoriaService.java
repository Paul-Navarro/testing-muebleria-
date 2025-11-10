package com.example.service;

import com.example.model.Categoria;
import com.example.repository.CategoriaRepository;
import com.example.repository.ProductoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    // ==== Lecturas ====

    @Transactional(readOnly = true)
    public List<Categoria> listAll(Sort sort) {
        return categoriaRepository.findAll(sort);
    }

    @Transactional(readOnly = true)
    public List<Categoria> listAll() {
        return categoriaRepository.findAll(Sort.by("nombre").ascending());
    }

    @Transactional(readOnly = true)
    public Categoria getById(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada: id=" + id));
    }

    @Transactional(readOnly = true)
    public Optional<Categoria> findBySlug(String slug) {
        return categoriaRepository.findBySlug(slug);
    }

    // ==== Escrituras ====

    public Categoria create(String nombre, String slug) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio.");
        }
        String finalSlug = slugify((slug != null && !slug.isBlank()) ? slug : nombre);

        if (categoriaRepository.existsBySlug(finalSlug)) {
            throw new IllegalArgumentException("Ya existe una categoría con el slug: " + finalSlug);
        }

        Categoria c = new Categoria(nombre.trim(), finalSlug);
        return categoriaRepository.save(c);
    }

    public Categoria update(Long id, String nombre, String slug) {
        Categoria c = getById(id);

        if (nombre != null && !nombre.isBlank()) {
            c.setNombre(nombre.trim());
        }

        if (slug != null) { // si viene null no tocamos el slug
            String newSlug = slug.isBlank() ? slugify(c.getNombre()) : slugify(slug);
            if (!newSlug.equals(c.getSlug()) && categoriaRepository.existsBySlug(newSlug)) {
                throw new IllegalArgumentException("Ya existe una categoría con el slug: " + newSlug);
            }
            c.setSlug(newSlug);
        }

        return categoriaRepository.save(c);
    }

    public void delete(Long id) {
        // Validar que no existan productos asociados antes de eliminar
        long productCount = productoRepository.countByCategoria_Id(id);
        if (productCount > 0) {
            throw new IllegalStateException(
                "No se puede eliminar la categoría porque tiene " + productCount + 
                " producto(s) asociado(s). Primero elimine o reasigne los productos a otra categoría."
            );
        }
        categoriaRepository.deleteById(id);
    }

    // ==== Util ====

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern DUPLICATE_DASH = Pattern.compile("-{2,}");

    /** Convierte texto a slug URL-safe: minus, sin acentos, sin espacios, con guiones. */
    public static String slugify(String input) {
        if (input == null) return null;
        String noWhitespace = WHITESPACE.matcher(input.trim()).replaceAll("-");
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD);
        String slug = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        slug = NONLATIN.matcher(slug).replaceAll("");
        slug = slug.toLowerCase();
        slug = DUPLICATE_DASH.matcher(slug).replaceAll("-");
        slug = slug.replaceAll("^-+|-+$", ""); // recortar guiones extremos
        if (slug.isBlank()) {
            throw new IllegalArgumentException("No se pudo generar un slug válido a partir de: " + input);
        }
        return slug;
    }
}
