package com.example.demoRFID.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable() // disable csrf protection
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(
                                "/api/products/**",
                                "/api/rfid/**",
                                "/api/sites/**",
                                "/swagger-ui/**",         // Swagger
                                "/v3/api-docs/**",        // Swagger API
                                "/swagger-resources/**",  // Swagger resources
                                "/webjars/**",
                                "/actuator/health/**",  //health
                                "/health/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic();

        return http.build();
    }
}
