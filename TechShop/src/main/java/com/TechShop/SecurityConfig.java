package com.TechShop;

import com.TechShop.domain.Ruta;
import com.TechShop.service.RutaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, @Lazy RutaService rutaService)
            throws Exception {

        var rutas = rutaService.getRutas();

        http.authorizeHttpRequests(requests -> {

            for (Ruta ruta : rutas) {

                if (!ruta.isRequiereRol()) {
                    requests.requestMatchers(ruta.getRuta()).permitAll();
                } else if (ruta.getRol().getRol().equals("ADMIN")) {
                    requests.requestMatchers(ruta.getRuta()).hasRole("ADMIN");
                } else if (ruta.getRol().getRol().equals("VENDEDOR")) {
                    requests.requestMatchers(ruta.getRuta()).hasAnyRole("ADMIN", "VENDEDOR");
                } else if (ruta.getRol().getRol().equals("USUARIO")) {
                    requests.requestMatchers(ruta.getRuta()).hasAnyRole("ADMIN", "USUARIO");
                }
            }

            requests.anyRequest().authenticated();
        });

        http.formLogin(form -> form
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
        ).sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
        );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configurerGlobal(AuthenticationManagerBuilder build,
            @Lazy PasswordEncoder passwordEncoder,
            @Lazy UserDetailsService userDetailsService) throws Exception {

        build.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }
}