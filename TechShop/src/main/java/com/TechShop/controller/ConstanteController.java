package com.TechShop.controller;

import com.TechShop.domain.Constante;
import com.TechShop.service.ConstanteService;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/constante")
public class ConstanteController {

    private final ConstanteService constanteService;

    public ConstanteController(ConstanteService constanteService) {
        this.constanteService = constanteService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var lista = constanteService.getConstantes();

        model.addAttribute("constantes", lista);
        model.addAttribute("constante", new Constante());
        model.addAttribute("totalConstantes", lista.size());

        return "/constante/listado";
    }

    @PostMapping("/guardar")
    public String guardar(Constante constante, RedirectAttributes redirectAttributes) {
        try {
            constanteService.save(constante);
            redirectAttributes.addFlashAttribute("todoOk", "Constante guardada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo guardar la constante.");
        }

        return "redirect:/constante/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idConstante, RedirectAttributes redirectAttributes) {
        try {
            constanteService.delete(idConstante);
            redirectAttributes.addFlashAttribute("todoOk", "Constante eliminada correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/constante/listado";
    }

    @GetMapping("/modificar/{idConstante}")
    public String modificar(@PathVariable Integer idConstante,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Constante constante = constanteService.getConstante(idConstante);
            model.addAttribute("constante", constante);
            return "/constante/modifica";
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", "La constante no existe.");
            return "redirect:/constante/listado";
        }
    }
}