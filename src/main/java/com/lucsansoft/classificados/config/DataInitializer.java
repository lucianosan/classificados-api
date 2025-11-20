package com.lucsansoft.classificados.config;

import com.lucsansoft.classificados.model.User;
import com.lucsansoft.classificados.service.AuthService;
import com.lucsansoft.classificados.service.ListingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataInitializer {
  @Bean CommandLineRunner seed(AuthService auth, ListingService listings){
    return args -> {
      try {
        // ensure admin exists
        User admin;
        try { admin = auth.login("admin@site.com", "admin123"); }
        catch(Exception e){ admin = auth.register("Administrador", "admin@site.com", "admin123"); admin.setRole("admin"); }
        // create minimal sample listings if none
        var all = listings.search(null,null,null);
        if (all.isEmpty()) {
          listings.create(admin.getId(), "iPhone 13", "iPhone 13 128GB, ótimo estado", new BigDecimal("3500.00"), "Eletrônicos", "São Paulo", "SP", "11999999999", List.of("/assets/iphone.jpg"));
          listings.create(admin.getId(), "Apartamento 2 quartos", "Apartamento bem localizado", new BigDecimal("250000.00"), "Imóveis", "Rio de Janeiro", "RJ", "21988888888", List.of("/assets/apto.jpg"));
        }
      } catch(Exception ignore) {}
    };
  }
}

