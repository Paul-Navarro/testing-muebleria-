// src/main/java/com/example/model/StripeEvent.java
package com.example.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "stripe_events", uniqueConstraints = @UniqueConstraint(columnNames = "eventId"))
public class StripeEvent {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String eventId;

  @Column(nullable = false)
  private OffsetDateTime processedAt = OffsetDateTime.now();

  public StripeEvent() {}
  public StripeEvent(String eventId) { this.eventId = eventId; }
 
}
