package com.TechShop.service;

import com.TechShop.domain.Rol;
import com.TechShop.domain.Usuario;
import com.TechShop.repository.RolRepository;
import com.TechShop.repository.UsuarioRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> getUsuarios(boolean activo) {
        if (activo) {
            return usuarioRepository.findByActivoTrue();
        }
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuario(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    @Transactional
    public void save(Usuario usuario) {
        final Integer idUser = usuario.getIdUsuario();

        Optional<Usuario> duplicadoUsername = usuarioRepository.findByUsername(usuario.getUsername());
        if (duplicadoUsername.isPresent()) {
            Usuario encontrado = duplicadoUsername.get();

            if (idUser == null || !encontrado.getIdUsuario().equals(idUser)) {
                throw new DataIntegrityViolationException("El username ya está en uso.");
            }
        }

        Optional<Usuario> duplicadoCorreo = usuarioRepository.findByUsernameOrCorreo(null, usuario.getCorreo());
        if (duplicadoCorreo.isPresent()) {
            Usuario encontrado = duplicadoCorreo.get();

            if (idUser == null || !encontrado.getIdUsuario().equals(idUser)) {
                throw new DataIntegrityViolationException("El correo ya está en uso.");
            }
        }

        if (usuario.getIdUsuario() == null) {
            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                throw new IllegalArgumentException("La contraseña es obligatoria.");
            }

            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            usuario.setActivo(true);

            Rol rolUsuario = rolRepository.findByRol("USUARIO")
                    .orElseThrow(() -> new IllegalArgumentException("No existe el rol USUARIO."));

            var roles = new HashSet<Rol>();
            roles.add(rolUsuario);
            usuario.setRoles(roles);

        } else {
            Usuario usuarioActual = usuarioRepository.findById(usuario.getIdUsuario())
                    .orElseThrow(() -> new IllegalArgumentException("El usuario no existe."));

            usuarioActual.setUsername(usuario.getUsername());
            usuarioActual.setNombre(usuario.getNombre());
            usuarioActual.setApellidos(usuario.getApellidos());
            usuarioActual.setCorreo(usuario.getCorreo());
            usuarioActual.setTelefono(usuario.getTelefono());
            usuarioActual.setRutaImagen(usuario.getRutaImagen());
            usuarioActual.setActivo(usuario.isActivo());

            if (usuario.getPassword() != null && !usuario.getPassword().isBlank()) {
                usuarioActual.setPassword(passwordEncoder.encode(usuario.getPassword()));
            }

            usuarioRepository.save(usuarioActual);
            return;
        }

        usuarioRepository.save(usuario);
    }

    @Transactional
    public void delete(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no existe."));

        usuarioRepository.delete(usuario);
    }
}