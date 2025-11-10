// src/main/java/com/example/repository/StripeEventRepository.java
package com.example.repository;

import com.example.model.StripeEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StripeEventRepository extends JpaRepository<StripeEvent, Long> {
  boolean existsByEventId(String eventId);
}
