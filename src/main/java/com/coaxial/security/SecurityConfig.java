package com.coaxial.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Autowired
    private Environment environment;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        String activeProfile = environment.getActiveProfiles().length > 0 ? 
            environment.getActiveProfiles()[0] : "dev";

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> {
                // Swagger endpoints - only in development (must come before /api/**)
                if ("dev".equals(activeProfile)) {
                    authz.requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**"
                    ).permitAll();
                }
                
                // Public endpoints
                authz.requestMatchers(
                    "/",
                    "/health",
                    "/actuator/health",
                    "/actuator/health/**",
                    "/api/health",
                    "/error", 
                    "/api/auth/login",
                    "/api/auth/logout",
                    "/api/auth/register",
                    "/api/auth/test",
                    "/api/auth/create-admin",
                    "/api/public/**"
                ).permitAll()
                
                // Admin endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // Instructor endpoints
                .requestMatchers("/api/instructor/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                
                // Student endpoints
                .requestMatchers("/api/student/**").hasAnyRole("STUDENT", "INSTRUCTOR", "ADMIN")
                
                // API endpoints - require JWT authentication (except auth endpoints)
                .requestMatchers("/api/**").authenticated()
                
                // All other requests
                .anyRequest().permitAll();
            })
            .userDetailsService(userDetailsService)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
