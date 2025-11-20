package com.lucsansoft.classificados.web;

import com.lucsansoft.classificados.model.Listing;
import com.lucsansoft.classificados.model.ListingImage;
import com.lucsansoft.classificados.service.FavoriteService;
import com.lucsansoft.classificados.service.ListingService;
import com.lucsansoft.classificados.web.dto.ListingDtos.CreateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/listings")
public class ListingController {
    private final ListingService listings;
    private final FavoriteService favorites;

    public ListingController(ListingService listings, FavoriteService favorites) {
        this.listings = listings;
        this.favorites = favorites;
    }

  @GetMapping
  public ResponseEntity<Map<String, Object>> search(@RequestParam(required = false) String q, @RequestParam(required = false) String category, @RequestParam(required = false) String city, @RequestParam(required = false, defaultValue = "false") boolean favoritesOnly, @RequestParam(required = false) java.util.UUID userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "12") int size) {
        java.util.List<Listing> all = listings.search(q, category, city);
        if (favoritesOnly && userId != null) {
            java.util.Set<java.util.UUID> favIds = favorites.listByUser(userId).stream().map(f -> f.getListing().getId()).collect(Collectors.toSet());
            all = all.stream().filter(l -> favIds.contains(l.getId())).collect(Collectors.toList());
  }

        int total = all.size();
        int from =Math.max(0,Math.min(page * size, total));
        int to =Math.max(from,Math.min(from + size, total));
        java.util.List<java.util.Map<String, Object>> items = all.subList(from, to).stream().map(this::toDto).collect(Collectors.toList());
        java.util.Map<String, Object> body = new java.util.LinkedHashMap<>();
        body.put("items", items);
        body.put("total", total);
        return ResponseEntity.ok(body);
    }

    @GetMapping(params = "id")
    public ResponseEntity<java.util.Map<String, Object>> getByQuery(@RequestParam String id) {
        java.util.Optional<Listing> byPid = id.length() <= 10 ? listings.getByPublicId(id) : java.util.Optional.empty();
        if (byPid.isPresent()) return ResponseEntity.ok(toDto(byPid.get()));
        try {
            java.util.UUID uid = java.util.UUID.fromString(id);
            return listings.get(uid).map(l -> ResponseEntity.ok(toDto(l))).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<java.util.Map<String, Object>> get(@PathVariable String id) {
        java.util.Optional<Listing> byPid = id.length() <= 10 ? listings.getByPublicId(id) : java.util.Optional.empty();
        if (byPid.isPresent()) return ResponseEntity.ok(toDto(byPid.get()));
        try {
            java.util.UUID uid = java.util.UUID.fromString(id);
            return listings.get(uid).map(l -> ResponseEntity.ok(toDto(l))).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<java.util.Map<String, Object>> create(@RequestBody CreateRequest req, org.springframework.security.core.Authentication a) {
        java.util.UUID owner = req.ownerId != null ? req.ownerId : java.util.UUID.fromString(a.getName());
        Listing l = listings.create(owner, req.title, req.description, req.price, req.category, req.city, req.state, req.contactPhone, req.images);
        return ResponseEntity.ok(toDto(l));
    }

    @PostMapping("/{id}/views")
    public ResponseEntity<Void> incrementViews(@PathVariable java.util.UUID id) {
        listings.incrementViews(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable java.util.UUID id) {
        listings.delete(id);
        return ResponseEntity.noContent().build();
    }

    private java.util.Map<String, Object> toDto(Listing l) {
        java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
        m.put("id", l.getPublicId()!=null? l.getPublicId() : l.getId().toString());
        m.put("uid", l.getId().toString());
        m.put("title", l.getTitle());
        m.put("description", l.getDescription());
        m.put("price", l.getPrice());
        m.put("category", l.getCategory());
        m.put("city", l.getCity());
        m.put("state", l.getState());
        m.put("contactPhone", l.getContactPhone());
        m.put("ownerId", l.getOwner().getId().toString());
        m.put("createdAt", l.getCreatedAt().toEpochMilli());
        m.put("views", l.getViews());
        m.put("isActive", l.getIsActive());
        m.put("images", l.getImages().stream().map(ListingImage::getUrl).collect(Collectors.toList()));
        return m;
    }
}
