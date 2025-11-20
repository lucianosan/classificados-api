package com.lucsansoft.classificados.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
public class SecurityConfig {
  @Value("${security.jwt.secret}") private String secret;
  @Bean JwtUtil jwtUtil(){ return new JwtUtil(secret); }
  @Bean SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf->csrf.disable());
    http.authorizeHttpRequests(auth->auth
      .requestMatchers("/api/auth/**","/v3/api-docs/**","/swagger-ui.html","/swagger-ui/**").permitAll()
      .requestMatchers(HttpMethod.GET, "/api/listings", "/api/listings/**", "/api/categories").permitAll()
      .requestMatchers(HttpMethod.POST, "/api/listings/*/views").permitAll()
      .anyRequest().authenticated()
    );
    http.addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
  @Bean OncePerRequestFilter jwtFilter(){
    return new OncePerRequestFilter(){
      @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        String h = req.getHeader("Authorization");
        if (h!=null && h.startsWith("Bearer ")){
          String t = h.substring(7);
          try{
            var claims = jwtUtil().verify(t);
            String sub = claims.getSubject(); String role = (String) claims.get("role");
            var auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(sub, null, List.of(new SimpleGrantedAuthority("ROLE_"+(role!=null?role:"user"))));
            org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);
          }catch(Exception e){ }
        }
        chain.doFilter(req,res);
      }
    };
  }
  @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration c) throws Exception { return c.getAuthenticationManager(); }
}
