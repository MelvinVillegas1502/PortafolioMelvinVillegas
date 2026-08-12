package com.TechShop;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/acceso_denegado").setViewName("acceso_denegado");
    }

    public static final String[] PUBLIC_URLS = {
        "/",
        "/index",
        "/login",
        "/acceso_denegado",
        "/css/**",
        "/js/**",
        "/img/**",
        "/images/**",
        "/webjars/**",
        "/favicon.ico",
        "/manifest.json",
        "/consultas/**"
    };

    public static final String[] ADMIN_URLS = {
        "/producto/nuevo",
        "/producto/guardar",
        "/producto/modificar/**",
        "/producto/eliminar/**",
        "/categoria/nuevo",
        "/categoria/guardar",
        "/categoria/modificar/**",
        "/categoria/eliminar/**",
        "/usuario/nuevo",
        "/usuario/guardar",
        "/usuario/modificar/**",
        "/usuario/eliminar/**"
    };

    public static final String[] ADMIN_OR_VENDEDOR_URLS = {
        "/producto/listado",
        "/categoria/listado",
        "/usuario/listado"
    };

    public static final String[] USUARIO_URLS = {
        "/carrito/**",
        "/facturar/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request -> request
                .requestMatchers(PUBLIC_URLS).permitAll()
                .requestMatchers(ADMIN_URLS).hasRole("ADMIN")
                .requestMatchers(ADMIN_OR_VENDEDOR_URLS).hasAnyRole("ADMIN", "VENDEDOR")
                .requestMatchers(USUARIO_URLS).hasRole("USUARIO")
                .anyRequest().authenticated()
        ).formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
        ).logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
        ).exceptionHandling(exceptions -> exceptions
                .accessDeniedPage("/acceso_denegado")
        );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService users(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
                .username("juan")
                .password(passwordEncoder.encode("123"))
                .roles("ADMIN")
                .build();

        UserDetails vendedor = User.builder()
                .username("rebeca")
                .password(passwordEncoder.encode("456"))
                .roles("VENDEDOR")
                .build();

        UserDetails usuario = User.builder()
                .username("pedro")
                .password(passwordEncoder.encode("789"))
                .roles("USUARIO")
                .build();

        return new InMemoryUserDetailsManager(admin, vendedor, usuario);
    }
}