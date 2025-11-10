// src/main/java/com/example/config/StripeConfig.java
package com.example.config;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class StripeConfig {

  @Value("${stripe.secret-key}")
  private String stripeSecretKey;

  @PostConstruct
  public void init() {
    Stripe.apiKey = stripeSecretKey; // Set global API key
  }
}
