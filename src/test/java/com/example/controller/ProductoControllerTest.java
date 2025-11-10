package com.example.controller;

import com.example.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.example.filter.JwtAuthenticationFilter;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
  controllers = ProductoController.class,
  excludeAutoConfiguration = SecurityAutoConfiguration.class,
  excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class ProductoControllerTest {
  @Autowired MockMvc mvc;
  @org.springframework.boot.test.mock.mockito.MockBean ProductoService productoService;

  @Test void deleteProducto_OK_204() throws Exception {
    doNothing().when(productoService).deleteProducto(1L);
    mvc.perform(delete("/api/productos/1")).andExpect(status().isNoContent());
  }

  @Test void deleteProducto_Conflict_409() throws Exception {
    doThrow(new IllegalStateException("tiene pedidos asociados")).when(productoService).deleteProducto(7L);
    mvc.perform(delete("/api/productos/7"))
      .andExpect(status().isConflict())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test void deleteProducto_NotFound_404() throws Exception {
    doThrow(new RuntimeException("No existe")).when(productoService).deleteProducto(99L);
    mvc.perform(delete("/api/productos/99"))
      .andExpect(status().isNotFound())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }
}
