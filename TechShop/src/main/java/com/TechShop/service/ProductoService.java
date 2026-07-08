package com.TechShop.service;

import java.math.BigDecimal;
import java.math.BigDecimal; 
import com.TechShop.domain.Producto;
import com.TechShop.repository.ProductoRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final FirebaseStorageService firebaseStorageService;

    public ProductoService(ProductoRepository productoRepository, FirebaseStorageService firebaseStorageService) {
        this.productoRepository = productoRepository;
        this.firebaseStorageService = firebaseStorageService;
    }

    @Transactional(readOnly = true)
    public List<Producto> getProductos(boolean activo) {
        if (activo) {
            return productoRepository.findByActivoTrue();
        }
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Producto> getProducto(Integer idProducto) {
        return productoRepository.findById(idProducto);
    }

    @Transactional
    public void save(Producto producto, MultipartFile imagenFile) {
        producto = productoRepository.save(producto);

        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                String rutaImagen = firebaseStorageService.uploadImage(
                        imagenFile,
                        "producto",
                        producto.getIdProducto()
                );

                producto.setRutaImagen(rutaImagen);
                productoRepository.save(producto);

            } catch (IOException e) {
            }
        }
    }

    @Transactional
    public void delete(Integer idProducto) {
        if (!productoRepository.existsById(idProducto)) {
            throw new IllegalArgumentException("El producto con ID " + idProducto + " no existe.");
        }

        try {
            productoRepository.deleteById(idProducto);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el producto. Tiene datos asociados.", e);
        }
    }
    @Transactional(readOnly = true)
public List<Producto> consultaDerivada(BigDecimal precioInf, BigDecimal precioSup) {
    return productoRepository.findByPrecioBetweenOrderByPrecioAsc(precioInf, precioSup);
}

@Transactional(readOnly = true)
public List<Producto> consultaJPQL(BigDecimal precioInf, BigDecimal precioSup) {
    return productoRepository.consultaJPQL(precioInf, precioSup);
}

@Transactional(readOnly = true)
public List<Producto> consultaSQL(BigDecimal precioInf, BigDecimal precioSup) {
    return productoRepository.consultaSQL(precioInf, precioSup);
}
}