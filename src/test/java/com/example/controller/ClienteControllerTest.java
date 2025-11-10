package com.example.controller;

import com.example.repository.ClienteRepository;
import com.example.repository.UserRepository;
import com.example.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

// ⬇️ excluimos tu filtro JWT para que no se cree el bean
import com.example.filter.JwtAuthenticationFilter;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
  controllers = ClienteController.class,
  excludeAutoConfiguration = SecurityAutoConfiguration.class,
  excludeFilters = @ComponentScan.Filter(
      type = FilterType.ASSIGNABLE_TYPE,
      classes = JwtAuthenticationFilter.class
  )
)
@AutoConfigureMockMvc(addFilters = false)
class ClienteControllerTest {

  @Autowired MockMvc mvc;

  // Mocks requeridos por el controller
  @org.springframework.boot.test.mock.mockito.MockBean ClienteService clienteService;
  @org.springframework.boot.test.mock.mockito.MockBean ClienteRepository clienteRepository;
  @org.springframework.boot.test.mock.mockito.MockBean UserRepository userRepository;
  @org.springframework.boot.test.mock.mockito.MockBean PasswordEncoder passwordEncoder;

  @Test
  void registrarCliente_OK_201() throws Exception {
    when(clienteRepository.existsByCedula("123")).thenReturn(false);
    when(clienteRepository.existsByEmail("mail@x.com")).thenReturn(false);
    when(userRepository.existsByUsername("jorgito")).thenReturn(false);
    when(passwordEncoder.encode("secreta")).thenReturn("hash");

    // simular saves que devuelven IDs
    when(clienteRepository.save(any())).thenAnswer(inv -> { var c = (com.example.model.Cliente) inv.getArgument(0); c.setId(1L); return c; });
    when(userRepository.save(any())).thenAnswer(inv -> { var u = (com.example.model.User) inv.getArgument(0); u.setId(10L); return u; });

    String body = """
      {"cedula":"123","email":"mail@x.com","username":"jorgito","password":"secreta","nombre":"Jorge"}
    """;

    mvc.perform(post("/api/clientes/registro").contentType(MediaType.APPLICATION_JSON).content(body))
       .andExpect(status().isCreated());
  }
}
