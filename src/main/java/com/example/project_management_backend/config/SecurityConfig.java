package com.example.project_management_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                        .requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()
                        .requestMatchers("/api/roles/**").hasRole("ADMIN")
                        .requestMatchers("/api/users/**").hasAnyRole("ADMIN","MENTOR","STUDENT")
                        .requestMatchers("/api/projects/**").hasAnyRole("ADMIN", "MENTOR","STUDENT") 
                        .requestMatchers("/api/tasks/**").hasAnyRole("ADMIN", "MENTOR", "STUDENT")
                        .requestMatchers("/api/skills/**").hasAnyRole("ADMIN","MENTOR","STUDENT")
                        .requestMatchers("/api/skill-mapping/**").hasAnyRole("ADMIN","MENTOR","STUDENT") 
                        
                        .requestMatchers(HttpMethod.POST, "/api/project-enrollment").hasAnyRole("STUDENT", "ADMIN")  
                        .requestMatchers(HttpMethod.GET, "/api/project-enrollment").hasAnyRole("ADMIN", "STUDENT")  
                        .requestMatchers(HttpMethod.DELETE, "/api/project-enrollment/**").hasAnyRole("STUDENT", "ADMIN")  

                        .requestMatchers("/api/stu-task/**").hasAnyRole("ADMIN","MENTOR","STUDENT") 
                        .requestMatchers("/api/tasks/*/comments").hasAnyRole("ADMIN","MENTOR","STUDENT")
                        .requestMatchers("/api/tasks/*/feedback").hasAnyRole("ADMIN","MENTOR","STUDENT")
                        
                        
                        .requestMatchers(HttpMethod.GET, "/api/project-skills/recommendations/**").hasAnyRole("STUDENT", "MENTOR", "ADMIN") // Fixed path issue
                        .requestMatchers(HttpMethod.POST, "/api/project-skills").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/project-skills").hasAnyRole("STUDENT", "MENTOR", "ADMIN")
                
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