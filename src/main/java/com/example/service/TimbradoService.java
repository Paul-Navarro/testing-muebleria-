package com.example.service;

import com.example.model.Timbrado;
import com.example.repository.TimbradoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TimbradoService {

    @Autowired
    private TimbradoRepository timbradoRepository;

    // Get all timbrados
    public List<Timbrado> getAllTimbrados() {
        return timbradoRepository.findAll();
    }

    // Get timbrado by ID
    public Optional<Timbrado> getTimbradoById(Long id) {
        return timbradoRepository.findById(id);
    }

    // Get timbrado by number
    public Optional<Timbrado> getTimbradoByNumber(String numeroTimbrado) {
        return timbradoRepository.findByNumeroTimbrado(numeroTimbrado);
    }

    // Get active timbrados by document type
    public List<Timbrado> getActivosByTipoDocumento(String tipoDocumento) {
        return timbradoRepository.findActivosByTipoDocumento(tipoDocumento, new Date());
    }

    // Get all active timbrados
    public List<Timbrado> getAllActivos() {
        return timbradoRepository.findAllActivos(new Date());
    }

    // Get the first active timbrado for FACTURA documents
    public Optional<Timbrado> getActiveTimbradoForFactura() {
        List<Timbrado> activos = getActivosByTipoDocumento("FACTURA");
        return activos.isEmpty() ? Optional.empty() : Optional.of(activos.get(0));
    }

    // Validate if a number is within authorized range
    public Optional<Timbrado> validateFacturaNumber(Long numeroFactura) {
        return timbradoRepository.findByTipoDocumentoAndNumeroEnRango("FACTURA", numeroFactura, new Date());
    }

    // Save or update timbrado
    public Timbrado saveTimbrado(Timbrado timbrado) {
        return timbradoRepository.save(timbrado);
    }

    // Delete timbrado
    public void deleteTimbrado(Long id) {
        timbradoRepository.deleteById(id);
    }

    // Update expired timbrados
    public void updateExpiredTimbrados() {
        List<Timbrado> expired = timbradoRepository.findVencidos(new Date());
        for (Timbrado timbrado : expired) {
            timbrado.setEstado(Timbrado.EstadoTimbrado.VENCIDO);
            timbradoRepository.save(timbrado);
        }
    }

    // Check if timbrado is valid for use
    public boolean isValidForUse(Timbrado timbrado) {
        if (timbrado == null) {
            return false;
        }
        
        Date now = new Date();
        return timbrado.getEstado() == Timbrado.EstadoTimbrado.ACTIVO &&
               timbrado.getFechaVencimiento().after(now) &&
               timbrado.getFechaInicio().before(now);
    }

    // Get timbrados by RUC
    public List<Timbrado> getTimbradosByRuc(String ruc) {
        return timbradoRepository.findByRuc(ruc);
    }

    // Create new timbrado
    public Timbrado createTimbrado(String numeroTimbrado, String numeroAutorizacion, 
                                  Date fechaInicio, Date fechaVencimiento, 
                                  String tipoDocumento, String ruc, String razonSocial) {
        Timbrado timbrado = new Timbrado();
        timbrado.setNumeroTimbrado(numeroTimbrado);
        timbrado.setNumeroAutorizacion(numeroAutorizacion);
        timbrado.setFechaInicio(fechaInicio);
        timbrado.setFechaVencimiento(fechaVencimiento);
        timbrado.setTipoDocumento(tipoDocumento);
        timbrado.setEstado(Timbrado.EstadoTimbrado.ACTIVO);
        timbrado.setRuc(ruc);
        timbrado.setRazonSocial(razonSocial);
        
        return saveTimbrado(timbrado);
    }
}