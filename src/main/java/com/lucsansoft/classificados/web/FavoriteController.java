package com.lucsansoft.classificados.web;

import com.lucsansoft.classificados.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;
@CrossOrigin(origins="*") @RestController @RequestMapping("/api/users/{userId}/favorites")
public class FavoriteController {
  private final FavoriteService favorites; public FavoriteController(FavoriteService favorites){this.favorites=favorites;}
  @GetMapping public ResponseEntity<java.util.List<java.util.Map<String,Object>>> list(@PathVariable java.util.UUID userId){
    return ResponseEntity.ok(favorites.listByUser(userId).stream().map(f->{ java.util.Map<String,Object> m=new java.util.LinkedHashMap<>(); m.put("listingId",f.getListing().getId().toString()); return m; }).collect(Collectors.toList())); }
  @PostMapping("/{listingId}") public ResponseEntity<Void> add(@PathVariable java.util.UUID userId, @PathVariable java.util.UUID listingId){ favorites.add(userId,listingId); return ResponseEntity.noContent().build(); }
  @DeleteMapping("/{listingId}") public ResponseEntity<Void> remove(@PathVariable java.util.UUID userId, @PathVariable java.util.UUID listingId){ favorites.remove(userId,listingId); return ResponseEntity.noContent().build(); }
}
