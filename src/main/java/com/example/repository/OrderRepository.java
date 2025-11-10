// src/main/java/com/example/repository/OrderRepository.java
package com.example.repository;

import com.example.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

  Optional<Order> findByProviderAndProviderRef(String provider, String providerRef);

  List<Order> findByClienteIdOrderByCreatedAtDesc(Long clienteId);
  Optional<Order> findByIdAndClienteId(Long id, Long clienteId);

  List<Order> findByClienteIdAndStatusOrderByCreatedAtDesc(Long clienteId, OrderStatus status);
  Optional<Order> findByIdAndClienteIdAndStatus(Long id, Long clienteId, OrderStatus status);

  Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);
  Page<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);

  @Query("SELECT o FROM Order o LEFT JOIN o.entrega e " +
         "WHERE e.estadoEntrega = :estado ORDER BY o.createdAt DESC")
  Page<Order> findByEstadoEntrega(@Param("estado") EstadoEntrega estado, Pageable pageable);

  @Query("SELECT o FROM Order o LEFT JOIN o.entrega e " +
         "WHERE o.status = :status AND e.estadoEntrega = :estado ORDER BY o.createdAt DESC")
  Page<Order> findByStatusAndEstadoEntrega(@Param("status") OrderStatus status,
                                           @Param("estado") EstadoEntrega estado,
                                           Pageable pageable);
}
