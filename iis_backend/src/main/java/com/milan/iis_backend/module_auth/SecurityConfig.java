package com.milan.iis_backend.module_auth;

import com.milan.iis_backend.module_auth.jwt.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable) // maknuo jer bi fe bacao login formu prilikom 401/403, i to mi je bas islo na zive
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"UNAUTHORIZED\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"FORBIDDEN\"}");
                        })
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // everyone can login
                        .requestMatchers("/api/auth/**").permitAll()
//                        .requestMatchers("/api/auth/refresh").hasAnyRole("USER", "ADMIN")
//                        .requestMatchers("/api/auth/revoke").hasAnyRole("USER", "ADMIN")
//
//                        // everyone can register :D
//                        .requestMatchers("/api/auth/register").permitAll()

                        // db access
                        .requestMatchers("/h2-console/**").permitAll()

                        // both users and admin can read data
                        .requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole("USER", "ADMIN")

                        // only admin can do create/update/delete
                        .requestMatchers(HttpMethod.POST, "/api/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")

                        // soap access
                        .requestMatchers("/ws/**").permitAll()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(h -> h.frameOptions(f -> f.disable()))
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}
