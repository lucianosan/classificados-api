package com.lucsansoft.classificados.web;

import com.lucsansoft.classificados.service.ListingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@CrossOrigin(origins="*") @RestController @RequestMapping("/api/categories")
public class CategoryController {
  private final ListingService listings; public CategoryController(ListingService listings){this.listings=listings;}
  @GetMapping public ResponseEntity<List<String>> list(){ return ResponseEntity.ok(listings.categories()); }
}
