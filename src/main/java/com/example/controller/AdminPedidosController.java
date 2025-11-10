// src/main/java/com/example/controller/AdminPedidosController.java
package com.example.controller;

import com.example.dto.admin.*;
import com.example.model.*;
import com.example.repository.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/pedidos")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPedidosController {

  private final OrderRepository orderRepo;
  private final EntregaPedidoRepository entregaRepo;

  public AdminPedidosController(OrderRepository orderRepo, EntregaPedidoRepository entregaRepo) {
    this.orderRepo = orderRepo;
    this.entregaRepo = entregaRepo;
  }

  // LISTA (paginada) con filtros simples
  @GetMapping
  public Page<AdminPedidoResumenDTO> listar(
      @RequestParam(required = false) OrderStatus estadoPago,
      @RequestParam(required = false) EstadoEntrega estadoEntrega,
      Pageable pageable
  ) {
    Page<Order> page;
    if (estadoPago != null && estadoEntrega != null) {
      page = orderRepo.findByStatusAndEstadoEntrega(estadoPago, estadoEntrega, pageable);
    } else if (estadoPago != null) {
      page = orderRepo.findByStatusOrderByCreatedAtDesc(estadoPago, pageable);
    } else if (estadoEntrega != null) {
      page = orderRepo.findByEstadoEntrega(estadoEntrega, pageable);
    } else {
      page = orderRepo.findAllByOrderByCreatedAtDesc(pageable);
    }

    return page.map(o -> {
      EntregaPedido e = o.getEntrega();
      AdminPedidoResumenDTO dto = new AdminPedidoResumenDTO();
      dto.setId(o.getId());
      dto.setFechaCreacion(o.getCreatedAt());
      dto.setTotal(o.getTotalAmount());
      dto.setEstadoPago(o.getStatus());
      dto.setEstadoEntrega(e != null ? e.getEstadoEntrega() : EstadoEntrega.PENDIENTE);

      if (o.getCliente() != null) {
        dto.setClienteId(o.getCliente().getId());
        dto.setClienteNombre(o.getCliente().getNombre());
        dto.setClienteTelefono(o.getCliente().getTelefono());
        dto.setClienteEmail(o.getCliente().getEmail());
      }
      dto.setIsDelivery(o.getIsDelivery());
      return dto;
    });
  }

  // DETALLE
  @GetMapping("/{id}")
  public ResponseEntity<AdminPedidoDetalleDTO> detalle(@PathVariable Long id) {
    return orderRepo.findById(id)
      .map(o -> {
        EntregaPedido e = o.getEntrega();

        AdminPedidoDetalleDTO dto = new AdminPedidoDetalleDTO();
        dto.setId(o.getId());
        dto.setFechaCreacion(o.getCreatedAt());
        dto.setTotal(o.getTotalAmount());
        dto.setMoneda(o.getCurrency());
        dto.setEstadoPago(o.getStatus());

        dto.setEstadoEntrega(e != null ? e.getEstadoEntrega() : EstadoEntrega.PENDIENTE);
        dto.setEntregadoEn(e != null ? e.getEntregadoEn() : null);

        if (e != null) {
          dto.setNombreContacto(e.getNombreContacto());
          dto.setTelefono(e.getTelefono());
          dto.setDireccionLinea1(e.getDireccionLinea1());
          dto.setDireccionLinea2(e.getDireccionLinea2());
          dto.setCiudad(e.getCiudad());
          dto.setDepartamento(e.getDepartamento());
          dto.setIndicaciones(e.getIndicaciones());
        }

        if (o.getCliente() != null) {
          dto.setClienteId(o.getCliente().getId());
          dto.setClienteNombre(o.getCliente().getNombre());
          dto.setClienteTelefono(o.getCliente().getTelefono());
          dto.setClienteEmail(o.getCliente().getEmail());
        }

        dto.setItems(o.getItems().stream()
          .map(i -> new AdminPedidoItemDTO(i.getProductId(), i.getProductName(), i.getQty(), i.getUnitPrice()))
          .collect(Collectors.toList())
        );

        return ResponseEntity.ok(dto);
      })
      .orElse(ResponseEntity.notFound().build());
  }

  // PATCH: actualizar estado de entrega
  @PatchMapping("/{id}/entrega")
  public ResponseEntity<?> actualizarEntrega(@PathVariable Long id, @RequestBody AdminEntregaActualizarDTO body) {
    return orderRepo.findById(id)
      .map(o -> {
        EntregaPedido e = o.getEntrega();

        // Si no hay snapshot de entrega, creamos uno mínimo que cumpla NOT NULL
        if (e == null) {
          e = new EntregaPedido();
          e.setPedido(o);

          // Datos mínimos para no violar NOT NULL (direccion_linea1 y, según tu esquema,
          // probablemente ciudad/departamento). Usamos datos del cliente cuando existan.
          Cliente c = o.getCliente();
          String nombre = c != null && notBlank(c.getNombre()) ? c.getNombre() : "Cliente";
          String tel    = c != null && notBlank(c.getTelefono()) ? c.getTelefono() : "-";

          e.setNombreContacto(nombre);
          e.setTelefono(tel);

          if (Boolean.TRUE.equals(o.getIsDelivery())) {
            // Delivery sin snapshot (caso excepcional): placeholders seguros
            e.setDireccionLinea1("SIN DIRECCIÓN");
            e.setCiudad("-");
            e.setDepartamento("-");
          } else {
            // Pick-up: marcamos explícitamente que es retiro
            e.setDireccionLinea1("PICK-UP");
            e.setCiudad("-");
            e.setDepartamento("-");
          }

          // Estado por defecto si no viene en el body
          e.setEstadoEntrega(body.getEstadoEntrega() != null ? body.getEstadoEntrega() : EstadoEntrega.PENDIENTE);
        }

        // Aplicar actualizaciones del body
        if (body.getEstadoEntrega() != null) {
          e.setEstadoEntrega(body.getEstadoEntrega());
          if (body.getEstadoEntrega() == EstadoEntrega.ENTREGADO &&
              e.getEntregadoEn() == null && body.getEntregadoEn() == null) {
            e.setEntregadoEn(OffsetDateTime.now());
          }
        }
        if (body.getEntregadoEn() != null) {
          e.setEntregadoEn(body.getEntregadoEn());
        }
        if (body.getIndicaciones() != null) {
          e.setIndicaciones(body.getIndicaciones());
        }

        entregaRepo.save(e);
        return ResponseEntity.ok().build();
      })
      .orElse(ResponseEntity.notFound().build());
  }

  // ===== helpers =====
  private static boolean notBlank(String s) {
    return s != null && !s.trim().isEmpty();
  }
}
