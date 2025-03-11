package com.example.project_management_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) 
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) 
                .authorizeHttpRequests(auth -> auth
                     
                        .requestMatchers("/api/auth/**").permitAll() 
                   
                        .requestMatchers("/api/roles/**").hasRole("ADMIN") 
                  
                        .requestMatchers("/api/users/**").permitAll() 
                        
                        .requestMatchers("/api/projects/**").hasAnyRole("ADMIN", "MENTOR") 
                        .requestMatchers("/api/tasks/**").hasAnyRole("ADMIN", "MENTOR") 
                        .requestMatchers("/api/skills/**").hasRole("ADMIN") 
                        .requestMatchers("/api/skill-mapping/**").permitAll() 
                        .requestMatchers("/api/skill-mapping/**").permitAll() 
                        .requestMatchers("/api/project-enrollment**").permitAll() 
                        .requestMatchers("/api/project-enrollment/enroll").hasRole("STUDENT")
                        .requestMatchers("/api/project-enrollment/**").hasRole("ADMIN") 
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class); 
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}