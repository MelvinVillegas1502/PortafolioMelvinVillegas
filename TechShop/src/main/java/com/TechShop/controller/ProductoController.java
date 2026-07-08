package com.TechShop.controller;

import com.TechShop.domain.Producto;
import com.TechShop.service.CategoriaService;
import com.TechShop.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/producto")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public ProductoController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var productos = productoService.getProductos(false);
        var categorias = categoriaService.getCategorias(true);

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("producto", new Producto());
        model.addAttribute("totalProductos", productos.size());

        return "/producto/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Producto producto,
            @RequestParam MultipartFile imagenFile,
            RedirectAttributes redirectAttributes) {

        if (producto.getCategoria() != null && producto.getCategoria().getIdCategoria() != null) {
            var categoriaOpt = categoriaService.getCategoria(producto.getCategoria().getIdCategoria());
            categoriaOpt.ifPresent(producto::setCategoria);
        }

        productoService.save(producto, imagenFile);

        redirectAttributes.addFlashAttribute("todoOk", "Producto guardado correctamente");

        return "redirect:/producto/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idProducto,
            RedirectAttributes redirectAttributes) {

        try {
            productoService.delete(idProducto);
            redirectAttributes.addFlashAttribute("todoOk", "Producto eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar el producto");
        }

        return "redirect:/producto/listado";
    }

    @GetMapping("/modificar/{idProducto}")
    public String modificar(@PathVariable("idProducto") Integer idProducto,
            Model model,
            RedirectAttributes redirectAttributes) {

        var productoOpt = productoService.getProducto(idProducto);

        if (productoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El producto no existe");
            return "redirect:/producto/listado";
        }

        var categorias = categoriaService.getCategorias(true);

        model.addAttribute("producto", productoOpt.get());
        model.addAttribute("categorias", categorias);

        return "/producto/modifica";
    }

    @GetMapping("/prueba")
    @ResponseBody
    public String prueba() {
        return "ProductoController funciona";
    }
}