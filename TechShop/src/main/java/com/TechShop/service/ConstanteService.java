package com.TechShop.service;

import com.TechShop.domain.Constante;
import com.TechShop.repository.ConstanteRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConstanteService {

    private final ConstanteRepository constanteRepository;

    public ConstanteService(ConstanteRepository constanteRepository) {
        this.constanteRepository = constanteRepository;
    }

    @Transactional(readOnly = true)
    public List<Constante> getConstantes() {
        return constanteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Constante getConstante(Integer idConstante) {
        return constanteRepository.findById(idConstante)
                .orElseThrow(() -> new NoSuchElementException("Constante no encontrada."));
    }

    @Transactional
    public void save(Constante constante) {
        constanteRepository.save(constante);
    }

    @Transactional
    public void delete(Integer idConstante) {
        if (!constanteRepository.existsById(idConstante)) {
            throw new IllegalArgumentException("La constante no existe.");
        }

        try {
            constanteRepository.deleteById(idConstante);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar la constante porque tiene datos asociados.", e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Constante> findByAtributo(String atributo) {
        return constanteRepository.findByAtributo(atributo);
    }
}