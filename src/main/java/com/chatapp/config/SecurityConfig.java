package com.chatapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Autowired
    @Lazy
    private JwtFilter jwtFilter;

    @Autowired
    private UuidAuthenticationProvider uuidAuthProvider;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    /*@Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }*/

    @Bean
    @Primary
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http
                .authenticationProvider(uuidAuthProvider) // Register your custom provider
                .getSharedObject(AuthenticationManagerBuilder.class)
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .httpBasic().disable() // 👈 disables the popup
                .formLogin().disable() // 👈 disables default form login too
                .authorizeHttpRequests()
                .antMatchers("/api/**/auth/**").permitAll()
                .antMatchers("/", "/login.html", "/login", "/chats.html", "/chats", "/chat.html", "/chat", "/health").permitAll()
                .antMatchers("/css/**", "/assets/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                .antMatchers("/api/**/admin/**").hasRole("ADMIN")
                .antMatchers("/ws/**", "/ws/info/**", "/app/**", "/topic/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    CommandLineRunner debugApp(Environment env) {
        return args -> {
            System.out.println("🛠️ Active Profile: " + Arrays.toString(env.getActiveProfiles()));
            System.out.println("🔍 Mongo URI: " + env.getProperty("spring.data.mongodb.uri"));
            System.out.println("🔍 MySQL URL: " + env.getProperty("spring.datasource.url"));
        };
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("https://192.168.*", "https://localhost:8080", "https://chat-app-production-e971.up.railway.app")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("*")
                        .allowCredentials(true); // ✅ must be true for cookie auth
            }
        };
    }
}
