package com.example.service;

import com.example.model.Timbrado;
import com.example.repository.TimbradoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
public class TimbradoInitializer implements CommandLineRunner {

    @Autowired
    private TimbradoRepository timbradoRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if we already have timbrados in the database
        if (timbradoRepository.count() == 0) {
            initializeTimbrados();
        }
    }

    private void initializeTimbrados() {
        // Create the current timbrado that was hardcoded in the PDF
        Timbrado timbradoFactura = new Timbrado();
        timbradoFactura.setNumeroTimbrado("17217403");
        timbradoFactura.setNumeroAutorizacion("SET-AUTH-2024-001");
        
        // Set dates (valid for 2 years from now)
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        
        timbradoFactura.setFechaInicio(Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        timbradoFactura.setFechaVencimiento(Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        
        timbradoFactura.setTipoDocumento("FACTURA");
        timbradoFactura.setNumeracionDesde(1L);
        timbradoFactura.setNumeracionHasta(999999L);
        timbradoFactura.setSerie("001");
        timbradoFactura.setEstablecimiento("001");
        timbradoFactura.setPuntoEmision("001");
        timbradoFactura.setEstado(Timbrado.EstadoTimbrado.ACTIVO);
        timbradoFactura.setRuc("80012345-6");
        timbradoFactura.setRazonSocial("LUNARIS MOBILIARIO");
        timbradoFactura.setActividadEconomica("Venta de muebles y decoración");
        
        timbradoRepository.save(timbradoFactura);

        // Create additional timbrado for receipts
        Timbrado timbradoRecibo = new Timbrado();
        timbradoRecibo.setNumeroTimbrado("17217404");
        timbradoRecibo.setNumeroAutorizacion("SET-AUTH-2024-002");
        timbradoRecibo.setFechaInicio(Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        timbradoRecibo.setFechaVencimiento(Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        timbradoRecibo.setTipoDocumento("RECIBO");
        timbradoRecibo.setNumeracionDesde(1L);
        timbradoRecibo.setNumeracionHasta(999999L);
        timbradoRecibo.setSerie("001");
        timbradoRecibo.setEstablecimiento("001");
        timbradoRecibo.setPuntoEmision("001");
        timbradoRecibo.setEstado(Timbrado.EstadoTimbrado.ACTIVO);
        timbradoRecibo.setRuc("80012345-6");
        timbradoRecibo.setRazonSocial("LUNARIS MOBILIARIO");
        timbradoRecibo.setActividadEconomica("Venta de muebles y decoración");
        
        timbradoRepository.save(timbradoRecibo);

        // Create a sample expired timbrado for testing
        Timbrado timbradoVencido = new Timbrado();
        timbradoVencido.setNumeroTimbrado("16217401");
        timbradoVencido.setNumeroAutorizacion("SET-AUTH-2023-001");
        
        LocalDate expiredStartDate = LocalDate.of(2023, 1, 1);
        LocalDate expiredEndDate = LocalDate.of(2023, 12, 31);
        
        timbradoVencido.setFechaInicio(Date.from(expiredStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        timbradoVencido.setFechaVencimiento(Date.from(expiredEndDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        timbradoVencido.setTipoDocumento("FACTURA");
        timbradoVencido.setNumeracionDesde(1L);
        timbradoVencido.setNumeracionHasta(500000L);
        timbradoVencido.setSerie("001");
        timbradoVencido.setEstablecimiento("001");
        timbradoVencido.setPuntoEmision("001");
        timbradoVencido.setEstado(Timbrado.EstadoTimbrado.VENCIDO);
        timbradoVencido.setRuc("80012345-6");
        timbradoVencido.setRazonSocial("LUNARIS MOBILIARIO");
        timbradoVencido.setActividadEconomica("Venta de muebles y decoración");
        
        timbradoRepository.save(timbradoVencido);

        System.out.println("✅ Timbrados inicializados:");
        System.out.println("   - Timbrado FACTURA activo: 17217403");
        System.out.println("   - Timbrado RECIBO activo: 17217404");
        System.out.println("   - Timbrado FACTURA vencido: 16217401");
    }
}