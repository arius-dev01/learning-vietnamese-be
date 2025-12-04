package com.example.vietjapaneselearning.security;

import com.example.vietjapaneselearning.enums.RoleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private JwtFilter filter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(configurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/api/login","/api/register", "/api/refresh_token", "/api/reset-password", "/api/forgot-password", "/api/game/*/start/*", "/api/user/me", "/api/vocabulary/download-file-format", "/api/question/download-file-format").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/user", "/api/vocabulary ","/api/game/recent-activities", "/api/lesson/top-lesson").hasAnyRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/game/*").permitAll()
                                .requestMatchers(HttpMethod.DELETE, "/api/lesson/delete_lesson/*").hasAnyRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/user/delete/*").hasAnyRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/user/edit-user").hasAnyRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/user/edit-profile").hasAnyRole("USER")

                                .requestMatchers(HttpMethod.PUT, "/api/user/translate-language").hasAnyRole("ADMIN", "USER")

                                .requestMatchers(HttpMethod.PUT, "/api/lesson/update_lesson", "/api/user/edit-profile").hasAnyRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/lesson/*", "/api/vocabulary/import","/api/vocabulary/add/*").hasAnyRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/add").hasAnyRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/game/*", "/api/game/*/*", "/api/game/*/topics/*/start","/api/topic/*").hasAnyRole("USER","ADMIN")
                                .requestMatchers("/api/user/*").hasAnyRole("ADMIN")
                                .anyRequest().authenticated())
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:4002", "http://192.168.1.109:4002"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Set-Cookie"));
        UrlBasedCorsConfigurationSource url = new UrlBasedCorsConfigurationSource();
        url.registerCorsConfiguration("/**", configuration);
        return url;
    }

}
