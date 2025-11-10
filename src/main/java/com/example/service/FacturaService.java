package com.example.service;

import com.example.dto.FacturaDTO;
import com.example.dto.DetalleFacturaDTO;
import com.example.model.DetalleFactura;
import com.example.model.Factura;
import com.example.model.MedioPago;
import com.example.model.Producto;
import com.example.model.Timbrado;
import com.example.model.Order;
import com.example.repository.FacturaRepository;
import com.example.repository.MedioPagoRepository;
import com.example.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private MedioPagoRepository medioPagoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TimbradoService timbradoService;

    // ===== CRUD básico que ya usabas =====

    public List<Factura> getAllFacturas() {
        return facturaRepository.findAll();
    }

    public Factura getFacturaById(Long id) {
        return facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));
    }

    public Factura createFactura(Factura factura) {
        // Asignar timbrado activo si no se especificó uno
        if (factura.getTimbrado() == null) {
            Timbrado timbradoActivo = timbradoService.getActiveTimbradoForFactura()
                .orElseThrow(() -> new RuntimeException("No hay timbrado activo disponible para facturas"));
            factura.setTimbrado(timbradoActivo);
        }

        // Vincular detalle -> factura (lado hijo)
        for (DetalleFactura detalle : factura.getDetalles()) {
            detalle.setFactura(factura);
        }

        // Calcular totales
        double subtotal = factura.getDetalles().stream()
                .mapToDouble(detalle -> (detalle.getPrecio() != null ? detalle.getPrecio() : 0d)
                        * (detalle.getCantidad() != null ? detalle.getCantidad() : 0))
                .sum();

        double ivaRate = 0.10; // ajusta según tu negocio
        double iva = subtotal * ivaRate;
        double total = subtotal + iva;

        factura.setSubtotal(subtotal);
        factura.setIva(iva);
        factura.setTotal(total);

        return facturaRepository.save(factura);
    }

    public Factura updateFactura(Long id, Factura facturaDetalles) {
        Factura facturaExistente = facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        facturaExistente.setFecha(facturaDetalles.getFecha());
        facturaExistente.setCliente(facturaDetalles.getCliente());
        facturaExistente.setDetalles(facturaDetalles.getDetalles());
        facturaExistente.setMedioPago(facturaDetalles.getMedioPago());

        // Calcular totales
        double subtotal = facturaDetalles.getDetalles().stream()
                .mapToDouble(detalle -> (detalle.getPrecio() != null ? detalle.getPrecio() : 0d)
                        * (detalle.getCantidad() != null ? detalle.getCantidad() : 0))
                .sum();

        double ivaRate = 0.10; // ajusta según tu negocio
        double iva = subtotal * ivaRate;
        double total = subtotal + iva;

        facturaExistente.setSubtotal(subtotal);
        facturaExistente.setIva(iva);
        facturaExistente.setTotal(total);

        return facturaRepository.save(facturaExistente);
    }

    public void deleteFactura(Long id) {
        if (!facturaRepository.existsById(id)) {
            throw new RuntimeException("Factura no encontrada");
        }
        facturaRepository.deleteById(id);
    }

    public FacturaDTO convertToDTO(Factura factura) {
        FacturaDTO dto = new FacturaDTO();
        dto.setId(factura.getId());
        dto.setFecha(factura.getFecha());
        dto.setClienteId(factura.getCliente().getId());
        
        // Datos del cliente para información de contacto
        dto.setNombreCliente(factura.getCliente().getNombre());
        dto.setApellidoCliente(factura.getCliente().getApellido());
        dto.setDireccionCliente(factura.getCliente().getDireccion());
        dto.setTelefonoCliente(factura.getCliente().getTelefono());
        dto.setEmailCliente(factura.getCliente().getEmail());
        dto.setCedulaCliente(factura.getCliente().getCedula());
        
        // Datos específicos de factura (pueden diferir del cliente)
        dto.setNombreFactura(factura.getNombreFactura());
        dto.setRucFactura(factura.getRucFactura());
        dto.setMedioPagoId(factura.getMedioPago().getId());
        dto.setMetodoPago(factura.getMedioPago().getTipo());
        
        // Agregar información del timbrado
        if (factura.getTimbrado() != null) {
            dto.setTimbradoId(factura.getTimbrado().getId());
            dto.setNumeroTimbrado(factura.getTimbrado().getNumeroTimbrado());
            dto.setTimbradoVencimiento(factura.getTimbrado().getFechaVencimiento() != null 
                ? factura.getTimbrado().getFechaVencimiento().toString() : null);
            dto.setTimbradoEstado(factura.getTimbrado().getEstado() != null 
                ? factura.getTimbrado().getEstado().toString() : null);
        }

        // Recalcular por si acaso 
        double subtotal = factura.getDetalles().stream()
                .mapToDouble(detalle -> (detalle.getPrecio() != null ? detalle.getPrecio() : 0d)
                        * (detalle.getCantidad() != null ? detalle.getCantidad() : 0))
                .sum();
        double iva = subtotal * 0.10; 
        double total = subtotal + iva;

        dto.setSubtotal(subtotal);
        dto.setIva(iva);
        dto.setTotal(total);

        List<DetalleFacturaDTO> detallesDTO = factura.getDetalles().stream()
                .map(detalle -> {
                    DetalleFacturaDTO det = new DetalleFacturaDTO();
                    det.setProductoId(detalle.getProducto() != null ? detalle.getProducto().getId() : null);
                    det.setNombreProducto(detalle.getProducto() != null ? detalle.getProducto().getNombre() : null);
                    det.setCantidad(detalle.getCantidad());
                    det.setPrecio(detalle.getPrecio());
                    return det;
                })
                .collect(Collectors.toList());

        dto.setDetalles(detallesDTO);
        return dto;
    }

    // ===== NUEVO: crear factura desde una Orden ya pagada =====

    /**
     * Construye y persiste una Factura a partir de una Order ya pagada.
     * @param order        Orden pagada (con cliente e ítems).
     * @param medioPagoId  ID del medio de pago (por ejemplo, el ID de "Stripe").
     */
    @Transactional
    public Factura crearDesdeOrden(Order order, Long medioPagoId) {
        if (order == null) throw new IllegalArgumentException("Orden nula");
        if (order.getCliente() == null) throw new IllegalStateException("Orden sin cliente");
        if (order.getItems() == null || order.getItems().isEmpty())
            throw new IllegalStateException("Orden sin ítems");

        MedioPago medioPago = medioPagoRepository.findById(medioPagoId)
                .orElseThrow(() -> new IllegalArgumentException("Medio de pago no encontrado: " + medioPagoId));

        // Obtener timbrado activo para facturas
        Timbrado timbradoActivo = timbradoService.getActiveTimbradoForFactura()
            .orElseThrow(() -> new RuntimeException("No hay timbrado activo disponible para facturas"));

        Factura factura = new Factura();
        factura.setFecha(new Date());
        factura.setCliente(order.getCliente());
        factura.setMedioPago(medioPago);
        factura.setTimbrado(timbradoActivo);
        
        // Setear datos específicos de factura desde la orden
        factura.setNombreFactura(order.getNombreFactura());
        factura.setRucFactura(order.getRucFactura());

        List<DetalleFactura> detalles = new ArrayList<>();
        order.getItems().forEach(oi -> {
            DetalleFactura d = new DetalleFactura();
            d.setFactura(factura);

            // Si existe el producto en tu catálogo, lo enlazamos (opcional).
            Producto prod = productoRepository.findById(oi.getProductId()).orElse(null);
            d.setProducto(prod);

            d.setCantidad(oi.getQty());
            d.setPrecio(oi.getUnitPrice() != null ? oi.getUnitPrice().doubleValue() : 0d);

            detalles.add(d);
        });
        factura.setDetalles(detalles);

        // Totales
        double subtotal = detalles.stream()
                .mapToDouble(det -> (det.getPrecio() != null ? det.getPrecio() : 0d)
                        * (det.getCantidad() != null ? det.getCantidad() : 0))
                .sum();

        double ivaRate = 0.10; // ajusta según tu régimen
        double iva = subtotal * ivaRate;
        double total = subtotal + iva;

        factura.setSubtotal(subtotal);
        factura.setIva(iva);
        factura.setTotal(total);

        return facturaRepository.save(factura);
    }
}
